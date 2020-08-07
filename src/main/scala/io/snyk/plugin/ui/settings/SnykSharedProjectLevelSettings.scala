package io.snyk.plugin.ui.settings

import java.util
import java.util.Collection

import io.snyk.plugin.IntellijLogging
import com.intellij.openapi.externalSystem.settings.{AbstractExternalSystemSettings, ExternalSystemSettingsListener}
import com.intellij.openapi.project.{ExternalStorageConfigurationManager, Project}
import com.intellij.openapi.components.{PersistentStateComponent, ServiceManager, State, Storage}
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemSettings.{State => ExternalSystemState}
import com.intellij.util.ThreeState
import com.intellij.util.containers.ContainerUtilRt
import com.intellij.util.xmlb.annotations.XCollection


/**
  * Holds shared project-level snyk-related settings (should be kept at the '*.ipr' or under '.idea').
  */
@State(name = "SnykSettings", storages = Array(new Storage("snyk.xml")))
class SnykSharedProjectLevelSettings(project: Project)
  extends AbstractExternalSystemSettings[SnykSharedProjectLevelSettings, SnykProjectSettings, SnykSystemSettingsListener](SnykSettingsTopic.Topic, project)
    with IntellijLogging
    with PersistentStateComponent[SnykSharedProjectLevelState] {

  override def subscribe(listener: ExternalSystemSettingsListener[SnykProjectSettings]): Unit =
    project.getMessageBus.connect(project)
      .subscribe(SnykSettingsTopic.Topic, new SnykSystemSettingsListenerAdapter(listener))

  override def copyExtraSettingsFrom(settings: SnykSharedProjectLevelSettings): Unit = {
    log.info("copyExtraSettingsFrom")
  }

  override def checkSettings(old: SnykProjectSettings, current: SnykProjectSettings): Unit = {
    log.info("checkSettings")
  }

  override def getState: SnykSharedProjectLevelState = new SnykSharedProjectLevelState

  override def loadState(state: SnykSharedProjectLevelState): Unit =
    super[AbstractExternalSystemSettings].loadState(state)

  override def getLinkedProjectsSettings: util.Collection[SnykProjectSettings] = {
    val settings: util.Collection[SnykProjectSettings] = super.getLinkedProjectsSettings
    val isStoredExternally = ExternalStorageConfigurationManager.getInstance(getProject).isEnabled

    settings.forEach(setting => {
      setting.setStoreProjectFilesExternally(ThreeState.fromBoolean(isStoredExternally))
    })

    settings
  }
}

class SnykSharedProjectLevelState extends ExternalSystemState[SnykProjectSettings] {
  private val projectSettingsSet: util.Set[SnykProjectSettings] = ContainerUtilRt.newTreeSet()

  @XCollection(elementTypes = Array(classOf[SnykProjectSettings]))
  override def getLinkedExternalProjectsSettings: util.Set[SnykProjectSettings] = projectSettingsSet

  override def setLinkedExternalProjectsSettings(settings: util.Set[SnykProjectSettings]): Unit =
    if (settings != null) {
      projectSettingsSet.addAll(settings)
    }
}

object SnykSharedProjectLevelSettings {
  def getInstance(project: Project): SnykSharedProjectLevelSettings =
    ServiceManager.getService(project, classOf[SnykSharedProjectLevelSettings]);
}