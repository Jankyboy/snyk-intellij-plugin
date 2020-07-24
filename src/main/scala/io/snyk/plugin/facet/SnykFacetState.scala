package io.snyk.plugin.facet

class SnykFacetState() {
  setFileName("")

  var fileName: String = _

  def getFileName: String = fileName

  def setFileName(newPath: String): Unit = fileName = newPath
}
