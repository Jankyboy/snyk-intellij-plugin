package io.snyk.plugin.facet

import com.intellij.facet._
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.module._
import com.intellij.openapi.module.Module
import io.snyk.plugin.ui.Icons
import javax.swing._


object SnykFacetType {
  val FACET_ID = "SNYK_FACET_ID"
  val FACET_NAME = "Snyk"
  val SNYK_FACET_TYPE_ID = new FacetTypeId[SnykFacet](FACET_ID)
}

class SnykFacetType()
  extends FacetType[SnykFacet, SnykFacetConfiguration](SnykFacetType.SNYK_FACET_TYPE_ID, SnykFacetType.FACET_ID, SnykFacetType.FACET_NAME) {

  override def createDefaultConfiguration = new SnykFacetConfiguration

  override def isSuitableModuleType(`type`: ModuleType[_ <: ModuleBuilder]) = true

  override def getIcon: Icon = Icons.snykLogo

  override def createFacet(
    module: Module,
    name: String,
    configuration: SnykFacetConfiguration,
    underlyingFacet: Facet[_ <: FacetConfiguration]): SnykFacet =
      new SnykFacet(this, module, name, configuration, underlyingFacet.asInstanceOf[Facet[SnykFacetConfiguration]])
}
