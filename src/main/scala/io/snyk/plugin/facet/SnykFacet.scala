package io.snyk.plugin.facet

import com.intellij.facet.{Facet, FacetType}
import com.intellij.openapi.module.Module

class SnykFacet(
  val facetType: FacetType[SnykFacet, SnykFacetConfiguration],
  val module: Module,
  val name: String,
  val facetConfiguration: SnykFacetConfiguration,
  val underlyingFacet: Facet[SnykFacetConfiguration])
    extends Facet[SnykFacetConfiguration](facetType, module, name, facetConfiguration, underlyingFacet) {
}
