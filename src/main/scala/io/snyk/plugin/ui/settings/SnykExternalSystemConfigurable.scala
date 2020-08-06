package io.snyk.plugin.ui.settings

import com.intellij.openapi.externalSystem.service.settings.AbstractExternalSystemConfigurable
import com.intellij.openapi.externalSystem.util.ExternalSystemSettingsControl
import com.intellij.openapi.project.Project

class SnykExternalSystemConfigurable(project: Project)
  extends AbstractExternalSystemConfigurable[SnykProjectSettings, SnykSystemSettingsListener, SnykSharedProjectLevelSettings](project, SnykConstants.SystemId) {

  val snykSystemSettings = SnykSystemSettings.getInstance(project)

  override def createProjectSettingsControl(settings: SnykProjectSettings): ExternalSystemSettingsControl[SnykProjectSettings] =
    new SnykProjectSettingsControl(settings)

  override def createSystemSettingsControl(settings: SnykSharedProjectLevelSettings): ExternalSystemSettingsControl[SnykSharedProjectLevelSettings] =
    new SnykSystemSettingsControl(settings)

  override def newProjectSettings(): SnykProjectSettings = new SnykProjectSettings

  override def getId: String = SnykExternalSystemConfigurable.HelpTopic
}

object SnykExternalSystemConfigurable {
  val HelpTopic = "reference.settingsdialog.project.snyk"
}
