package snyk.container.ui

import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.ui.components.labels.LinkLabel
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.uiDesigner.core.Spacer
import icons.SnykIcons
import io.snyk.plugin.Severity
import io.snyk.plugin.getKubernetesImageCache
import snyk.container.BaseImageInfo
import snyk.container.BaseImageVulnerabilities
import snyk.container.ContainerIssuesForImage
import snyk.container.KubernetesWorkloadImage
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea

class BaseImageRemediationDetailPanel(
    private val project: Project,
    private val imageIssues: ContainerIssuesForImage
) : JPanel() {

    private val targetImages: List<KubernetesWorkloadImage> = getKubernetesImageCache(project)
        ?.getKubernetesWorkloadImages()
        ?.filter { it.image == imageIssues.imageName }
        ?: emptyList()

    init {
        this.layout = GridLayoutManager(10, 1, Insets(20, 10, 20, 20), -1, 0)

        this.add(
            Spacer(),
            baseGridConstraints(
                9,
                anchor = GridConstraints.ANCHOR_CENTER,
                fill = GridConstraints.FILL_VERTICAL,
                HSizePolicy = GridConstraints.SIZEPOLICY_CAN_SHRINK,
                VSizePolicy = GridConstraints.SIZEPOLICY_WANT_GROW,
                indent = 0
            )
        )

        if (imageIssues.baseImageRemediationInfo != null) {
            this.add(
                baseRemediationInfoPanel(),
                panelGridConstraints(8)
            )
        }

        this.add(
            getTitlePanel(),
            panelGridConstraints(0)
        )

        this.name = "BaseImageRemediationDetailPanel"
    }

    private fun baseRemediationInfoPanel(): JPanel {
        val panel = JPanel(GridLayoutManager(4, 4, Insets(20, 0, 0, 0), 0, 10))

        panel.add(
            JLabel("Recommendations for upgrading the base image"),
            baseGridConstraints(0)
        )

        panel.add(
            innerRemediationInfoPanel(),
            baseGridConstraints(1)
        )

        return panel
    }

    private fun innerRemediationInfoPanel(): JPanel {
        val panel = JPanel(GridLayoutManager(3, 4, Insets(0, 0, 0, 0), 0, 10))

        imageIssues.baseImageRemediationInfo?.currentImage?.let {
            addBaseImageInfo(panel, 0, CURRENT_IMAGE, it)
        }

        imageIssues.baseImageRemediationInfo?.minorUpgrades?.let {
            addBaseImageInfo(panel, 1, MINOR_UPGRADES, it)
        }

        imageIssues.baseImageRemediationInfo?.alternativeUpgrades?.let {
            addBaseImageInfo(panel, 2, ALTERNATIVE_UPGRADES, it)
        }

        return panel
    }

    private fun addBaseImageInfo(panel: JPanel, row: Int, title: String, info: BaseImageInfo) {
        panel.add(
            JLabel(title).apply {
                font = io.snyk.plugin.ui.getFont(Font.BOLD, -1, JLabel().font)
            },
            baseGridConstraints(row, 0, indent = 0)
        )
        panel.add(
            JLabel(info.name).apply {
                name = title
            },
            baseGridConstraints(row, 1, indent = 5)
        )
        panel.add(
            getVulnerabilitiesCHMLpanel(info.vulnerabilities),
            baseGridConstraints(row, 2, indent = 5)
        )
    }

    private fun panelGridConstraints(row: Int) = baseGridConstraints(
        row = row,
        anchor = GridConstraints.ANCHOR_CENTER,
        fill = GridConstraints.FILL_BOTH,
        HSizePolicy = GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
        VSizePolicy = GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
        indent = 0
    )

    private fun baseGridConstraints(
        row: Int,
        column: Int = 0,
        rowSpan: Int = 1,
        colSpan: Int = 1,
        anchor: Int = GridConstraints.ANCHOR_WEST,
        fill: Int = GridConstraints.FILL_NONE,
        HSizePolicy: Int = GridConstraints.SIZEPOLICY_FIXED,
        VSizePolicy: Int = GridConstraints.SIZEPOLICY_FIXED,
        minimumSize: Dimension? = null,
        preferredSize: Dimension? = null,
        maximumSize: Dimension? = null,
        indent: Int = 1,
        useParentLayout: Boolean = false
    ): GridConstraints {
        return GridConstraints(
            row, column, rowSpan, colSpan, anchor, fill, HSizePolicy, VSizePolicy, minimumSize, preferredSize,
            maximumSize, indent, useParentLayout
        )
    }

    private fun getVulnerabilitiesCHMLpanel(vulnerabilities: BaseImageVulnerabilities?): JPanel {
        val panel = JPanel()
        panel.layout = GridLayoutManager(1, 4, Insets(0, 0, 0, 0), 5, 0)

        if (vulnerabilities == null) return panel

        panel.add(
            getSeverityCountItem(vulnerabilities.critical, Severity.CRITICAL),
            baseGridConstraints(0, column = 0, indent = 0)
        )
        panel.add(
            getSeverityCountItem(vulnerabilities.high, Severity.HIGH),
            baseGridConstraints(0, column = 1, indent = 0)
        )
        panel.add(
            getSeverityCountItem(vulnerabilities.medium, Severity.MEDIUM),
            baseGridConstraints(0, column = 2, indent = 0)
        )
        panel.add(
            getSeverityCountItem(vulnerabilities.low, Severity.LOW),
            baseGridConstraints(0, column = 3, indent = 0)
        )

        return panel
    }

    private fun getSeverityCountItem(count: Int, severity: String): JPanel {
        val panel = JPanel()
        panel.layout = GridLayoutManager(1, 2, Insets(0, 0, 0, 0), 0, 0)

        val baseColor = Severity.getColor(severity)
        panel.add(
            JLabel("%3d ".format(count)).apply {
                font = io.snyk.plugin.ui.getFont(Font.BOLD, 14, JTextArea().font)
                foreground = baseColor
            },
            baseGridConstraints(0, column = 0, indent = 0)
        )
        panel.add(
            JLabel(" ").apply {
                icon = SnykIcons.getSeverityIcon(severity)
            },
            baseGridConstraints(0, column = 1, indent = 0)
        )

        panel.isOpaque = true
        panel.background = Severity.getBgColor(severity)// UIUtil.mix(Color.WHITE, baseColor, 0.25)

        return panel
    }

    private fun getTitlePanel(): JPanel {
        val titlePanel = JPanel()
        titlePanel.layout = GridLayoutManager(2, 1, Insets(0, 0, 0, 0), -1, 5)

        val titleLabel = JLabel().apply {
            font = io.snyk.plugin.ui.getFont(Font.BOLD, 20, font)
            text = " ${imageIssues.imageName}"
            icon = SnykIcons.CONTAINER_IMAGE_24
        }

        titlePanel.add(
            titleLabel,
            baseGridConstraints(0)
        )

        titlePanel.add(
            secondRowTitlePanel(),
            baseGridConstraints(1)
        )

        return titlePanel
    }

    private fun secondRowTitlePanel(): Component {
        val panel = JPanel()

        val affectedFile2Line = targetImages.map { Pair(it.psiFile, it.lineNumber) }
        val columnCount = 5 + affectedFile2Line.count()
        panel.layout = GridLayoutManager(1, columnCount, Insets(0, 0, 0, 0), 5, 0)

        panel.add(
            JLabel("Image"),
            baseGridConstraints(0, column = 0, indent = 0)
        )
        addSeparator(panel, 1)

        panel.add(
            JLabel("${imageIssues.uniqueCount} vulnerabilities"),
            baseGridConstraints(0, column = 2, indent = 0)
        )
        addSeparator(panel, 3)

        // todo: fix UI
        affectedFile2Line.forEachIndexed { index, (file, line) ->
            val targetFileName = file.name + ":" + line
            panel.add(
                LinkLabel.create(targetFileName) {
                    navigateToTargetFile(file, line - 1) // to 1-based count used in the editor
                },
                baseGridConstraints(0, column = 4 + index, indent = 0)
            )
        }
        return panel
    }

    private fun navigateToTargetFile(file: PsiFile, line: Int) {
        if (file.isValid) {
            val document = PsiDocumentManager.getInstance(project).getDocument(file)
            if (document != null) {
                val lineNumber = if (0 <= line && line < document.lineCount) line else 0
                val lineStartOffset = document.getLineStartOffset(lineNumber)
                PsiNavigationSupport.getInstance().createNavigatable(
                    project,
                    file.virtualFile,
                    lineStartOffset
                ).navigate(false)
            }
        }
    }

    private fun addSeparator(panel: JPanel, column: Int) {
        panel.add(
            JLabel("|").apply { makeOpaque(this, 50) },
            baseGridConstraints(0, column = column, indent = 0)
        )
    }

    private fun makeOpaque(component: JComponent, alpha: Int) {
        component.foreground = Color(
            component.foreground.red,
            component.foreground.green,
            component.foreground.blue,
            alpha)
    }

    companion object {
        const val CURRENT_IMAGE = "Current image"
        const val MINOR_UPGRADES = "Minor upgrades"
        const val ALTERNATIVE_UPGRADES = "Alternative upgrades"
    }
}
