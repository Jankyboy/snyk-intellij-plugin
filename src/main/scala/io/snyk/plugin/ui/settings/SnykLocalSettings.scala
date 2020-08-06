package io.snyk.plugin.ui.settings

import com.intellij.openapi.components.{ServiceManager, State, Storage, StoragePathMacros}
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemLocalSettings
import com.intellij.openapi.project.Project

@State(
  name = "SnykLocalSettings",
  storages = Array(
    new Storage(StoragePathMacros.CACHE_FILE),
    new Storage(value = StoragePathMacros.WORKSPACE_FILE, deprecated = true)
  )
)
class SnykLocalSettings(project: Project)
  extends AbstractExternalSystemLocalSettings[SnykLocalSettingsState](SnykConstants.SystemId, project, new SnykLocalSettingsState)  {
}

object SnykLocalSettings {
  def getInstance(project: Project): SnykLocalSettings = ServiceManager.getService(project, classOf[SnykLocalSettings])
}

class SnykLocalSettingsState extends AbstractExternalSystemLocalSettings.State {
}
