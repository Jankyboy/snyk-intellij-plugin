package io.snyk.plugin.ui.settings

import com.intellij.openapi.externalSystem.settings.ExternalProjectSettings
import com.intellij.util.ThreeState
import com.intellij.util.xmlb.annotations.Transient

class SnykProjectSettings extends ExternalProjectSettings {
  private var additionalParameters = ""

  private var storeProjectFilesExternally = ThreeState.NO

  @Transient
  def getStoreProjectFilesExternally: ThreeState = storeProjectFilesExternally

  def setStoreProjectFilesExternally(value: ThreeState): Unit = {
    storeProjectFilesExternally = value
  }

  def getAdditionalParameters: String = additionalParameters

  def setAdditionalParameters(newParams: String): Unit = additionalParameters = newParams

  override def clone(): SnykProjectSettings = {
    val projectSettings = new SnykProjectSettings

    copyTo(projectSettings)

    projectSettings.additionalParameters = additionalParameters

    projectSettings
  }
}
