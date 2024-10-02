@file:JvmName("UtilsKt")
@file:Suppress("unused")

package io.snyk.plugin

import com.intellij.codeInsight.codeVision.CodeVisionHost
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.icons.ExpUiIcons.Run
import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.Alarm
import com.intellij.util.messages.Topic
import com.jetbrains.rd.generator.nova.PredefinedType
import io.snyk.plugin.analytics.AnalyticsScanListener
import io.snyk.plugin.services.SnykApplicationSettingsStateService
import io.snyk.plugin.services.SnykCliAuthenticationService
import io.snyk.plugin.services.SnykProjectSettingsStateService
import io.snyk.plugin.services.SnykTaskQueueService
import io.snyk.plugin.services.download.SnykCliDownloaderService
import io.snyk.plugin.settings.SnykProjectSettingsConfigurable
import io.snyk.plugin.ui.SnykBalloonNotificationHelper
import io.snyk.plugin.ui.toolwindow.SnykToolWindowFactory
import io.snyk.plugin.ui.toolwindow.SnykToolWindowPanel
import org.apache.commons.lang3.SystemUtils
import org.jetbrains.concurrency.runAsync
import snyk.common.ProductType
import snyk.common.SnykCachedResults
import snyk.common.UIComponentFinder
import snyk.common.isSnykTenant
import snyk.common.lsp.ScanInProgressKey
import snyk.common.lsp.ScanIssue
import snyk.common.lsp.ScanState
import snyk.container.ContainerService
import snyk.container.KubernetesImageCache
import snyk.errorHandler.SentryErrorReporter
import snyk.iac.IacScanService
import java.io.File
import java.io.FileNotFoundException
import java.net.URI
import java.nio.file.Path
import java.util.Objects.nonNull
import java.util.SortedSet
import java.util.concurrent.TimeUnit
import javax.swing.JComponent

private val logger = Logger.getInstance("#io.snyk.plugin.UtilsKt")

fun getIacService(project: Project): IacScanService? = project.serviceIfNotDisposed()

fun getKubernetesImageCache(project: Project): KubernetesImageCache? = project.serviceIfNotDisposed()

fun getSnykTaskQueueService(project: Project): SnykTaskQueueService? = project.serviceIfNotDisposed()

fun getSnykToolWindowPanel(project: Project): SnykToolWindowPanel? = project.serviceIfNotDisposed()

fun getSnykCachedResults(project: Project): SnykCachedResults? = project.serviceIfNotDisposed()

fun getSnykCachedResultsForProduct(project: Project, product: ProductType): MutableMap<SnykFile, List<ScanIssue>>? {
    return when (product) {
        ProductType.OSS -> getSnykCachedResults(project)?.currentOSSResultsLS
        ProductType.IAC -> getSnykCachedResults(project)?.currentIacResultsLS
        ProductType.CONTAINER -> getSnykCachedResults(project)?.currentContainerResultsLS
        ProductType.CODE_SECURITY -> getSnykCachedResults(project)?.currentSnykCodeResultsLS
        ProductType.CODE_QUALITY -> getSnykCachedResults(project)?.currentSnykCodeResultsLS
    }
}

fun getAnalyticsScanListener(project: Project): AnalyticsScanListener? = project.serviceIfNotDisposed()

fun getContainerService(project: Project): ContainerService? = project.serviceIfNotDisposed()

fun getSnykCliAuthenticationService(project: Project?): SnykCliAuthenticationService? = project?.serviceIfNotDisposed()

fun getSnykCliDownloaderService(): SnykCliDownloaderService = getApplicationService()

fun getSnykProjectSettingsService(project: Project): SnykProjectSettingsStateService? = project.serviceIfNotDisposed()

fun getCliFile() = File(pluginSettings().cliPath)

fun isCliInstalled(): Boolean = ApplicationManager.getApplication().isUnitTestMode || getCliFile().exists()

fun pluginSettings(): SnykApplicationSettingsStateService = getApplicationService()

fun getPluginPath() = PathManager.getPluginsPath() + "/snyk-intellij-plugin"

fun isProjectSettingsAvailable(project: Project?) = nonNull(project) && !project!!.isDefault

fun snykToolWindow(project: Project): ToolWindow? {
    return ToolWindowManager.getInstance(project).getToolWindow(SnykToolWindowFactory.SNYK_TOOL_WINDOW)
}

// see project.service<T>() in com.intellij.openapi.components
private inline fun <reified T : Any> Project.serviceIfNotDisposed(): T? {
    if (this.isDisposed) return null
    return try {
        getService(T::class.java)
    } catch (t: Throwable) {
        SentryErrorReporter.captureException(t)
        null
    }
}

/**
 * Copy of [com.intellij.openapi.components.service] to make code compilable with jvm 11 bytecode (Idea 2022.1)
 */
private inline fun <reified T : Any> getApplicationService(): T {
    val serviceClass = T::class.java
    return ApplicationManager.getApplication()?.getService(serviceClass)
        ?: throw RuntimeException("Cannot find service ${serviceClass.name} (classloader=${serviceClass.classLoader})")
}

fun <L : Any> getSyncPublisher(project: Project, topic: Topic<L>): L? {
    val messageBus = project.messageBus
    if (messageBus.isDisposed) return null
    return messageBus.syncPublisher(topic)
}

val <T> List<T>.head: T
    get() = first()

fun isAdditionalParametersValid(params: String?): Boolean {
    params.isNullOrEmpty() && return true

    val list = params!!.split(" ")
    return !list.contains("-d")
}

fun isUrlValid(url: String?): Boolean {
    url.isNullOrEmpty() && return true

    return try {
        val uri = url?.let { URI.create(it) }
        return uri?.isSnykTenant() ?: false
    } catch (throwable: Throwable) {
        false
    }
}

fun isOssRunning(project: Project): Boolean {
    return isProductScanRunning(project, ProductType.OSS)
}

private fun isProductScanRunning(project: Project, productType: ProductType): Boolean {
    return isProductScanRunning(project, productType, null)
}

private fun isProductScanRunning(
    project: Project,
    productType: ProductType,
    progressIndicator: ProgressIndicator?
): Boolean {
    val lsRunning = project.getContentRootVirtualFiles().any { vf ->
        val key = ScanInProgressKey(vf, productType)
        ScanState.scanInProgress[key] == true
    }
    return lsRunning ||
        (progressIndicator != null && progressIndicator.isRunning && !progressIndicator.isCanceled)
}

fun isSnykCodeRunning(project: Project): Boolean {
    return isProductScanRunning(project, ProductType.CODE_SECURITY) || isProductScanRunning(
        project,
        ProductType.CODE_QUALITY
    )
}

fun isIacRunning(project: Project): Boolean {
    return isProductScanRunning(project, ProductType.IAC)
}

fun isContainerRunning(project: Project): Boolean {
    return isProductScanRunning(
        project,
        ProductType.CONTAINER,
        getSnykTaskQueueService(project)?.containerScanProgressIndicator
    )
}

fun isScanRunning(project: Project): Boolean =
    isOssRunning(project) || isSnykCodeRunning(project) || isIacRunning(project) || isContainerRunning(project)

fun isCliDownloading(): Boolean = getSnykCliDownloaderService().isCliDownloading()

// check sastEnablement in a loop with rising timeout
private val alarm = Alarm()

fun controlExternalProcessWithProgressIndicator(
    indicator: ProgressIndicator,
    onCancel: () -> Unit
) {
    lateinit var checkCancelled: () -> Unit
    checkCancelled = {
        if (indicator.isCanceled) {
            onCancel()
        } else {
            alarm.addRequest(checkCancelled, 100)
        }
    }
    checkCancelled.invoke()
}

fun isFileListenerEnabled(): Boolean = pluginSettings().fileListenerEnabled

fun isDocumentationHoverEnabled(): Boolean = Registry.get("snyk.isDocumentationHoverEnabled").asBoolean()

fun isPreCommitCheckEnabled(): Boolean = Registry.get("snyk.issuesBlockCommit").asBoolean()

fun getWaitForResultsTimeout(): Long =
    Registry.intValue(
        "snyk.timeout.results.waiting",
        DEFAULT_TIMEOUT_FOR_SCAN_WAITING_MS
    ).toLong()

const val DEFAULT_TIMEOUT_FOR_SCAN_WAITING_MIN = 12L
val DEFAULT_TIMEOUT_FOR_SCAN_WAITING_MS =
    TimeUnit.MILLISECONDS.convert(DEFAULT_TIMEOUT_FOR_SCAN_WAITING_MIN, TimeUnit.MINUTES).toInt()

fun findPsiFileIgnoringExceptions(virtualFile: VirtualFile, project: Project): PsiFile? {
    return if (!virtualFile.isValid || project.isDisposed) {
        null
    } else {
        try {
            var psiFile: PsiFile? = null
            ReadAction.run<RuntimeException> {
                psiFile = PsiManager.getInstance(project).findFile(virtualFile)
            }
            return psiFile
        } catch (ignored: Throwable) {
            null
        }
    }
}

fun refreshAnnotationsForOpenFiles(project: Project) {
    if (project.isDisposed || ApplicationManager.getApplication().isDisposed) return
    runAsync {
        VirtualFileManager.getInstance().asyncRefresh()

        val openFiles = FileEditorManager.getInstance(project).openFiles

        ApplicationManager.getApplication().invokeLater {
            if (!project.isDisposed) {
                project.service<CodeVisionHost>().invalidateProvider(CodeVisionHost.LensInvalidateSignal(null))
            }
        }
        openFiles.forEach {
            val psiFile = findPsiFileIgnoringExceptions(it, project)
            if (psiFile != null) {
                invokeLater {
                    if (!psiFile.project.isDisposed) {
                        DaemonCodeAnalyzer.getInstance(project).restart(psiFile)
                    }
                }
            }
        }
    }
}

fun navigateToSource(
    project: Project,
    virtualFile: VirtualFile,
    selectionStartOffset: Int,
    selectionEndOffset: Int? = null,
) {
    runAsync {
        if (!virtualFile.isValid) return@runAsync
        val textLength = virtualFile.contentsToByteArray().size
        if (selectionStartOffset in (0 until textLength)) {
            // jump to Source
            val navigatable =
                PsiNavigationSupport.getInstance().createNavigatable(
                    project,
                    virtualFile,
                    selectionStartOffset,
                )
            if (navigatable.canNavigateToSource()) {
                invokeLater {
                    navigatable.navigate(false)
                }
            }
        } else {
            logger.warn("Navigation to wrong offset: $selectionStartOffset with file length=$textLength")
        }

        if (selectionEndOffset != null) {
            // highlight(by selection) suggestion range in source file
            if (selectionEndOffset in (0 until textLength) &&
                selectionStartOffset < selectionEndOffset
            ) {
                invokeLater {
                    val editor = FileEditorManager.getInstance(project).selectedTextEditor
                    editor?.selectionModel?.setSelection(selectionStartOffset, selectionEndOffset)
                }
            } else {
                logger.warn("Selection of wrong range: [$selectionStartOffset:$selectionEndOffset]")
            }
        }
    }
}

// `Memory leak` deceted by Jetbrains, see details here:
// https://youtrack.jetbrains.com/issue/IJSDK-979/Usage-of-ShowSettingsUtilshowSettingsDialogProject-jClassT-will-cause-Memory-leak-detected-KotlinCompilerConfigurableTab
fun showSettings(project: Project, componentNameToFocus: String, componentHelpHint: String) {
    ShowSettingsUtil.getInstance()
        .showSettingsDialog(project, SnykProjectSettingsConfigurable::class.java) {
            val componentToFocus = UIComponentFinder.getComponentByName(
                it.snykSettingsDialog.getRootPanel(),
                JComponent::class,
                componentNameToFocus
            )
            if (componentToFocus != null) {
                it.snykSettingsDialog.runBackgroundable({
                    componentToFocus.requestFocusInWindow()
                    SnykBalloonNotificationHelper
                        .showInfoBalloonForComponent(componentHelpHint, componentToFocus, true)
                }, delayMillis = 1000)
            } else {
                logger.warn("Can't find component with name: $componentNameToFocus")
            }
        }
}

fun String.suffixIfNot(suffix: String): String {
    if (!this.endsWith(suffix)) {
        return this + suffix
    }
    return this
}

fun String.prefixIfNot(prefix: String): String {
    if (!this.startsWith(prefix)) {
        return prefix + this
    }
    return this
}

fun VirtualFile.contentRoot(project: Project): VirtualFile? {
    var file: VirtualFile? = null
    ReadAction.run<RuntimeException> {
        file = ProjectRootManager.getInstance(project).fileIndex.getContentRootForFile(this)
    }
    return file
}

fun PsiFile.contentRoot(project: Project) = this.virtualFile.contentRoot(project)
fun PsiFile.relativePathToContentRoot(project: Project): Path? =
    this.contentRoot(project)?.toNioPath()?.relativize(this.virtualFile.toNioPath())

private const val END_INDEX_FOR_OS_MATCHING = 3

fun getOS(): String {
    val osMap = mapOf(
        "mac" to "macOS",
        "win" to "windows",
        "lin" to "linux"
    )
    val osName = SystemUtils.OS_NAME.toString().lowercase().substring(0, END_INDEX_FOR_OS_MATCHING)
    return osMap[osName] ?: osName
}

fun getArch(): String {
    val value: String = SystemUtils.OS_ARCH.toString().lowercase()
    val archMap = mapOf(
        "x8664" to "x86_64",
        "amd64" to "x86_64",
        "arm64" to "arm64",
        "aarch64" to "arm64",
        "x8632" to "386",
        "386" to "386",
    )
    return archMap[value] ?: value
}

fun String.toVirtualFile(): VirtualFile {
    return if (!this.startsWith("file://")) {
        StandardFileSystems.local().refreshAndFindFileByPath(this) ?: throw FileNotFoundException(this)
    } else {
        VirtualFileManager.getInstance().refreshAndFindFileByUrl(this.toVirtualFileURL())
            ?: throw FileNotFoundException(this)
    }
}

// add a slash when on windows
fun VirtualFile.toLanguageServerURL(): String {
    if (this.urlContainsDriveLetter()) {
        return this.url.replace("file://", "file:///")
    }
    return this.url
}

// remove first "/" if on windows
fun String.toVirtualFileURL(): String {
    if (this.isWindowsURI() && this.startsWith("file:///") && this.length > 10 && this.substring(9, 10) == ":") {
        return this.replaceFirst("/", "")
    }
    return this
}

fun String.isWindowsURI() = SystemUtils.IS_OS_WINDOWS && this.startsWith("file://")

fun VirtualFile.urlContainsDriveLetter() =
    this.url.isWindowsURI() && this.url.length > 9 && this.url.substring(8, 9) == ":"

fun VirtualFile.getPsiFile(project: Project): PsiFile? {
    return runReadAction { PsiManager.getInstance(project).findFile(this) }
}

fun VirtualFile.getDocument(): Document? = runReadAction { FileDocumentManager.getInstance().getDocument(this) }

fun Project.getContentRootPaths(): SortedSet<Path> {
    return getContentRootVirtualFiles()
        .mapNotNull { it.path.toNioPathOrNull() }
        .toSortedSet()
}

fun Project.getContentRootVirtualFiles(): Set<VirtualFile> {
    if (this.isDisposed) return emptySet()
    var contentRoots = ProjectRootManager.getInstance(this).contentRoots
    if (contentRoots.isEmpty()) {
        // this should cover for the case when no content roots are configured, e.g. in rider
        contentRoots = ProjectManager.getInstance().openProjects
            .mapNotNull { it.basePath?.toVirtualFile() }.toTypedArray()
    }

    // the sort is to ensure that parent folders come first
    // e.g. /a/b should come before /a/b/c
    return contentRoots
        .filter { it.exists() && it.isDirectory }
        .sortedBy { it.path }.toSet()
}

fun VirtualFile.isInContent(project: Project): Boolean {
    val vf = this
    return ReadAction.compute<Boolean, RuntimeException> { ProjectFileIndex.getInstance(project).isInContent(vf) }
}
