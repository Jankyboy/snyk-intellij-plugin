package io.snyk.plugin.ui.settings

import com.intellij.openapi.externalSystem.settings.ExternalProjectSettings

class SnykProjectSettings extends ExternalProjectSettings {
  private var additionalParameters = ""

  def getAdditionalParameters(): String = additionalParameters

  def setAdditionalParameters(newParams: String) = additionalParameters = newParams
}
