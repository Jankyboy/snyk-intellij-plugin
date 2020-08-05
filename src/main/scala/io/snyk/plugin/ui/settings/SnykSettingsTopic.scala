package io.snyk.plugin.ui.settings

import com.intellij.util.messages.{Topic => IntelliJTopic}

object SnykSettingsTopic {
  val Topic: IntelliJTopic[SnykSystemSettingsListener] =
    IntelliJTopic.create("Snyk-specific settings", classOf[SnykSystemSettingsListener])
}
