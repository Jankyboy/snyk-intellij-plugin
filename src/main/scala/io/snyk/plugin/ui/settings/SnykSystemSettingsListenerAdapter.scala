package io.snyk.plugin.ui.settings

import com.intellij.openapi.externalSystem.settings.{DelegatingExternalSystemSettingsListener, ExternalSystemSettingsListener}

class SnykSystemSettingsListenerAdapter(listener: ExternalSystemSettingsListener[SnykProjectSettings])
  extends DelegatingExternalSystemSettingsListener[SnykProjectSettings](listener)
    with SnykSystemSettingsListener {

}
