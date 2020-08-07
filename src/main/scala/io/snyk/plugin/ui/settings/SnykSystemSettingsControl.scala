package io.snyk.plugin.ui.settings

import java.awt.{Dimension, Insets}

import com.intellij.openapi.externalSystem.util.{ExternalSystemSettingsControl, ExternalSystemUiUtil, PaintAwarePanel}
import com.intellij.uiDesigner.core.Spacer
import com.intellij.uiDesigner.core.{GridConstraints => UIGridConstraints, GridLayoutManager => UIGridLayoutManager}
import javax.swing.{JCheckBox, JLabel, JPanel, JTextField}

class SnykSystemSettingsControl(settings: SnykSharedProjectLevelSettings) extends ExternalSystemSettingsControl[SnykSharedProjectLevelSettings] {

  private val customEndpointTextField: JTextField = new JTextField()
  private val organizationTextField: JTextField = new JTextField()
  private val ignoreUnknownCACheckBox: JCheckBox = new JCheckBox()

  override def fillUi(canvas: PaintAwarePanel, indentLevel: Int): Unit = {
    val gridLayoutManager = new UIGridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1)

    val rootPanel: JPanel = new JPanel()
    rootPanel.setLayout(gridLayoutManager)

    val customEndpointLabel = new JLabel
    customEndpointLabel.setText("Custom endpoint:")

    rootPanel.add(customEndpointLabel, new UIGridConstraints(
      0,
      0,
      1,
      1,
      UIGridConstraints.ANCHOR_WEST,
      UIGridConstraints.FILL_NONE,
      UIGridConstraints.SIZEPOLICY_FIXED,
      UIGridConstraints.SIZEPOLICY_FIXED,
      null,
      new Dimension(110, 16),
      null,
      0,
      false))

    val spacer = new Spacer()

    rootPanel.add(spacer, new UIGridConstraints(
      4,
      0,
      1,
      2,
      UIGridConstraints.ANCHOR_CENTER,
      UIGridConstraints.FILL_VERTICAL,
      1,
      UIGridConstraints.SIZEPOLICY_WANT_GROW,
      null,
      new Dimension(110, 14),
      null,
      0,
      false))

    rootPanel.add(customEndpointTextField, new UIGridConstraints(
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

    val organizationLabel = new JLabel
    organizationLabel.setText("Organization:")

    rootPanel.add(organizationLabel, new UIGridConstraints(
      2,
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
    rootPanel.add(organizationTextField, new UIGridConstraints(
      2,
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

    ignoreUnknownCACheckBox.setText("Ignore unknown CA")

    rootPanel.add(ignoreUnknownCACheckBox, new UIGridConstraints(
      1,
      1,
      1,
      1,
      UIGridConstraints.ANCHOR_WEST,
      UIGridConstraints.FILL_NONE,
      UIGridConstraints.SIZEPOLICY_CAN_SHRINK | UIGridConstraints.SIZEPOLICY_CAN_GROW,
      UIGridConstraints.SIZEPOLICY_FIXED,
      null,
      null,
      null,
      0,
      false))

    canvas.add(rootPanel, ExternalSystemUiUtil.getFillLineConstraints(indentLevel))
  }

  override def reset(): Unit = {

  }

  override def isModified: Boolean = false

  override def apply(settings: SnykSharedProjectLevelSettings): Unit = {

  }

  override def validate(settings: SnykSharedProjectLevelSettings): Boolean = true

  override def disposeUIResources(): Unit = {

  }

  override def showUi(show: Boolean): Unit = {
    ExternalSystemUiUtil.showUi(this, show)
  }
}
