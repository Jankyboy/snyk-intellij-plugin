package io.snyk.plugin.facet

import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.util.Comparing
import javax.swing._
import java.awt._

object SnykFacetEditorTab {
  private val FACET_PANEL_PROMPT = "File name: "
}

class SnykFacetEditorTab(
  val snykFacetSettings: SnykFacetState,
  val editorContext: FacetEditorContext,
  val validatorsManager: FacetValidatorsManager) extends FacetEditorTab {

  val fileNameTextField: JTextField = new JTextField(snykFacetSettings.getFileName)

  override def createComponent: JComponent = {
    val mainPanel = new JPanel(new BorderLayout)

    mainPanel.add(new JLabel(SnykFacetEditorTab.FACET_PANEL_PROMPT), BorderLayout.WEST)
    mainPanel.add(fileNameTextField)

    val facetPanel = new JPanel(new BorderLayout)

    facetPanel.add(mainPanel, BorderLayout.NORTH)
    facetPanel
  }

  override def getDisplayName: String = SnykFacetType.FACET_NAME

  override def isModified: Boolean = !Comparing.equal(snykFacetSettings.getFileName, fileNameTextField.getText.trim)

  @throws[ConfigurationException]
  override def apply(): Unit = try {
    val newTextContent = fileNameTextField.getText

    snykFacetSettings.setFileName(newTextContent)
  } catch {
    case exception: Exception =>
      throw new ConfigurationException(exception.toString)
  }

  override def reset(): Unit = fileNameTextField.setText(snykFacetSettings.getFileName)
}
