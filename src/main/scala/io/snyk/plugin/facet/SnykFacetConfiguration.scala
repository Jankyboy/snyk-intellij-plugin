package io.snyk.plugin.facet

import com.intellij.facet.FacetConfiguration
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.components.PersistentStateComponent

class SnykFacetConfiguration extends FacetConfiguration with PersistentStateComponent[SnykFacetState] {
  private var facetState: SnykFacetState = new SnykFacetState

  override def getState: SnykFacetState = facetState

  override def loadState(state: SnykFacetState): Unit = facetState = state

  override def createEditorTabs(
    context: FacetEditorContext,
    manager: FacetValidatorsManager): Array[FacetEditorTab] =
      Array[FacetEditorTab](new SnykFacetEditorTab(facetState, context, manager))
}
