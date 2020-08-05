package io.snyk.plugin.ui.settings

import java.util
import java.util.Set

import io.snyk.plugin.IntellijLogging
import com.intellij.openapi.externalSystem.settings.{AbstractExternalSystemSettings, ExternalSystemSettingsListener}
import com.intellij.openapi.project.Project
import com.intellij.openapi.components.{PersistentStateComponent, State, Storage}
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemSettings.{State => ExternalSystemState}
import com.intellij.util.containers.ContainerUtilRt
import com.intellij.util.xmlb.annotations.XCollection
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings

/**
  * Holds shared project-level snyk-related settings (should be kept at the '*.ipr' or under '.idea').
  */
@State(name = "SnykSettings", storages = Array(new Storage("snyk.xml")))
class SnykSystemSettings(project: Project)
  extends AbstractExternalSystemSettings[SnykSystemSettings, SnykProjectSettings, SnykSystemSettingsListener](SnykSettingsTopic.Topic, project)
    with IntellijLogging
    with PersistentStateComponent[SnykSystemSettingsState] {

  override def subscribe(listener: ExternalSystemSettingsListener[SnykProjectSettings]): Unit =
    project.getMessageBus.connect(project).subscribe(SnykSettingsTopic.Topic, new SnykSystemSettingsListenerAdapter(listener))

  override def copyExtraSettingsFrom(settings: SnykSystemSettings): Unit = {
    log.info("copyExtraSettingsFrom")
  }

  override def checkSettings(old: SnykProjectSettings, current: SnykProjectSettings): Unit = {
    log.info("checkSettings")
  }

  override def getState: SnykSystemSettingsState = new SnykSystemSettingsState

  override def loadState(state: SnykSystemSettingsState): Unit = super.loadState(state)
}

class SnykSystemSettingsState extends ExternalSystemState[SnykProjectSettings] {
  private val projectSettingsSet = ContainerUtilRt.newTreeSet

  @XCollection(elementTypes = classOf[SnykProjectSettings])
  override def getLinkedExternalProjectsSettings: util.Set[SnykProjectSettings] = projectSettingsSet

  override def setLinkedExternalProjectsSettings(settings: util.Set[SnykProjectSettings]): Unit =
    if (settings != null) {
      projectSettingsSet.addAll(settings)
    }
}
