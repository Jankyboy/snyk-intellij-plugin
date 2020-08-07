package io.snyk.plugin.ui.settings

import java.io.File

import com.intellij.execution.configurations.SimpleJavaParameters
import com.intellij.openapi.externalSystem.{ExternalSystemConfigurableAware, ExternalSystemManager}
import com.intellij.openapi.externalSystem.model.project.{ModuleData, ProjectData}
import com.intellij.openapi.externalSystem.model.{DataNode, ProjectKeys, ProjectSystemId}
import com.intellij.openapi.externalSystem.model.task.{ExternalSystemTaskId, ExternalSystemTaskNotificationListener}
import com.intellij.openapi.externalSystem.service.project.ExternalSystemProjectResolver
import com.intellij.openapi.externalSystem.task.ExternalSystemTaskManager
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util
import com.intellij.util.{Function => IntelliJFunction}
import io.snyk.plugin.IntellijLogging
import org.jetbrains.plugins.gradle.util.GradleConstants

class SnykExternalSystemManager
  extends ExternalSystemManager[SnykProjectSettings, SnykSystemSettingsListener, SnykSharedProjectLevelSettings, SnykLocalSettings, SnykExternalSystemExecutionSettings]
    with ExternalSystemConfigurableAware
    with IntellijLogging {

  override def getSystemId: ProjectSystemId = SnykConstants.SystemId

  override def getSettingsProvider: IntelliJFunction[Project, SnykSharedProjectLevelSettings] =
    (project: Project) => SnykSharedProjectLevelSettings.getInstance(project)

  override def getLocalSettingsProvider: IntelliJFunction[Project, SnykLocalSettings] =
    (project: Project) => SnykLocalSettings.getInstance(project)

  override def getExecutionSettingsProvider: IntelliJFunction[util.Pair[Project, String], SnykExternalSystemExecutionSettings] =
    (pair: util.Pair[Project, String]) => new SnykExternalSystemExecutionSettings

  override def getProjectResolverClass: Class[_ <: ExternalSystemProjectResolver[SnykExternalSystemExecutionSettings]] =
    classOf[SnykExternalSystemProjectResolver]

  override def getTaskManagerClass: Class[_ <: ExternalSystemTaskManager[SnykExternalSystemExecutionSettings]] =
    classOf[SnykExternalSystemTaskManager]

  override def getExternalProjectDescriptor: FileChooserDescriptor = {
    new FileChooserDescriptor(false, false, false, false, false, false)
  }

  override def enhanceRemoteProcessing(parameters: SimpleJavaParameters): Unit = {
    log.info("enhanceRemoteProcessing")
  }

  override def getConfigurable(project: Project): Configurable = new SnykExternalSystemConfigurable(project)
}

class SnykExternalSystemProjectResolver extends ExternalSystemProjectResolver[SnykExternalSystemExecutionSettings] {
  override def resolveProjectInfo(
    id: ExternalSystemTaskId,
    projectPath: String,
    isPreviewMode: Boolean,
    settings: SnykExternalSystemExecutionSettings,
    listener: ExternalSystemTaskNotificationListener): DataNode[ProjectData] = {

    val projectFileName = new File(projectPath).getName
    val projectData = new ProjectData(GradleConstants.SYSTEM_ID, projectFileName, projectPath, projectPath)

    val projectDataNode = new DataNode[ProjectData](ProjectKeys.PROJECT, projectData, null)

    projectDataNode.createChild(
      ProjectKeys.MODULE,
      new ModuleData(projectFileName, SnykConstants.SystemId, getDefaultModuleTypeId, projectFileName, projectPath, projectPath))

    projectDataNode
  }

  def getDefaultModuleTypeId: String = {
    val moduleType = ModuleTypeManager.getInstance.getDefaultModuleType
    moduleType.getId
  }

  override def cancelTask(
    taskId: ExternalSystemTaskId,
    listener: ExternalSystemTaskNotificationListener) = true
}

class SnykExternalSystemTaskManager extends ExternalSystemTaskManager[SnykExternalSystemExecutionSettings] {

  override def cancelTask(
    id: ExternalSystemTaskId,
    listener: ExternalSystemTaskNotificationListener): Boolean = true
}
