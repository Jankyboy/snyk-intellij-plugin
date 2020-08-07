package io.snyk.plugin.ui.settings

import java.awt.{Dimension, Insets}

import com.intellij.openapi.externalSystem.service.settings.AbstractExternalProjectSettingsControl
import com.intellij.openapi.externalSystem.util.{ExternalSystemUiUtil, PaintAwarePanel}
import com.intellij.uiDesigner.core.{GridConstraints => UIGridConstraints, GridLayoutManager => UIGridLayoutManager}
import javax.swing.{JLabel, JPanel, JTextField}

class SnykProjectSettingsControl(snykProjectSettings: SnykProjectSettings)
  extends AbstractExternalProjectSettingsControl[SnykProjectSettings](snykProjectSettings) {

  val rootPanel: JPanel = new JPanel()

  override def fillExtraControls(contentPanel: PaintAwarePanel, indentLevel: Int): Unit = {
    val gridLayoutManager = new UIGridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1)

    val additionalParametersTextField: JTextField = new JTextField("")
    rootPanel.setLayout(gridLayoutManager)

    val additionalParametersLabel = new JLabel
    additionalParametersLabel.setText("Additional parameters:")

    rootPanel.add(additionalParametersLabel, new UIGridConstraints(
      0,
      0,
      1,
      1,
      UIGridConstraints.ANCHOR_WEST,
      UIGridConstraints.FILL_NONE,
      UIGridConstraints.SIZEPOLICY_FIXED,
      UIGridConstraints.SIZEPOLICY_FIXED,
      null,
      null,
      null,
      0,
      false))

    rootPanel.add(additionalParametersTextField, new UIGridConstraints(
      0,
      1,
      1,
      1,
      UIGridConstraints.ANCHOR_WEST,
      UIGridConstraints.FILL_HORIZONTAL,
      UIGridConstraints.SIZEPOLICY_WANT_GROW,
      UIGridConstraints.SIZEPOLICY_FIXED,
      null,
      new Dimension(150, -1),
      null,
      0,
      false))

    contentPanel.add(rootPanel, ExternalSystemUiUtil.getFillLineConstraints(indentLevel))
  }


  override def showUi(show: Boolean): Unit = {
    super.showUi(show)

    ExternalSystemUiUtil.showUi(this, show)
  }

  override def isExtraSettingModified: Boolean = false

  override def resetExtraSettings(isDefaultModuleCreation: Boolean): Unit = {
  }

  override def applyExtraSettings(settings: SnykProjectSettings): Unit = {
  }

  override def validate(settings: SnykProjectSettings): Boolean = true
}
