package io.snyk.plugin.ui.toolwindow

import ai.deepcode.javaclient.core.SuggestionForFile
import javax.swing.tree.DefaultMutableTreeNode

class SuggestionTreeNode(suggestion: SuggestionForFile) : DefaultMutableTreeNode(suggestion)
