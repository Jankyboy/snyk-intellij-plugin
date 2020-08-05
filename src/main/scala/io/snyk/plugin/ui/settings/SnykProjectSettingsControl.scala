package io.snyk.plugin.ui.settings

import com.intellij.openapi.externalSystem.service.settings.AbstractExternalProjectSettingsControl
import com.intellij.openapi.externalSystem.util.PaintAwarePanel
import com.intellij.openapi.project.Project

class SnykProjectSettingsControl(snykProjectSettings: SnykProjectSettings)
  extends AbstractExternalProjectSettingsControl[SnykProjectSettings](snykProjectSettings) {

  private val controlBuilder: SnykProjectSettingsControlBuilder = new SnykProjectSettingsControlBuilder

  override def fillExtraControls(content: PaintAwarePanel, indentLevel: Int): Unit =
    controlBuilder.createAndFillControls(content, indentLevel)

  override def isExtraSettingModified: Boolean = controlBuilder.isModified()

  override def resetExtraSettings(isDefaultModuleCreation: Boolean): Unit = {
    //controlBuilder.reset(snykProjectSettings.get)
  }

  override def applyExtraSettings(settings: SnykProjectSettings): Unit = controlBuilder.apply(settings)

  override def validate(settings: SnykProjectSettings): Boolean = controlBuilder.validate(settings)
}

class SnykProjectSettingsControlBuilder {
  def createAndFillControls(content: PaintAwarePanel, indentLevel: Int) = {

  }

  def isModified(): Boolean = {
    false
  }

  def reset(project: Project, projectSettings: SnykProjectSettings, isDefaultModuleCreation: Boolean) = {
  }

  def apply(settings: SnykProjectSettings) = {

  }

  def validate(projectSettings: SnykProjectSettings): Boolean = {
    true
  }
}
