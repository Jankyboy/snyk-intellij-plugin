package io.snyk.plugin.ui.jcef

import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.awt.Color

fun Color.toHex() = ThemeBasedStylingGenerator.toCssHex(this)

class ThemeBasedStylingGenerator {
    companion object {
        fun toCssHex(color: Color): String {
            return "#%02x%02x%02x".format(color.red, color.green, color.blue)
        }
    }

    fun shift(
        colorComponent: Int,
        d: Double,
    ): Int {
        val n = (colorComponent * d).toInt()
        return n.coerceIn(0, 255)
    }

    fun getCodeDiffColors(
        baseColor: Color,
        isHighContrast: Boolean,
    ): Pair<Color, Color> {
        val addedColor =
            if (isHighContrast) {
                Color(28, 68, 40) // high contrast green
            } else {
                Color(shift(baseColor.red, 0.75), baseColor.green, shift(baseColor.blue, 0.75))
            }

        val removedColor =
            if (isHighContrast) {
                Color(84, 36, 38) // high contrast red
            } else {
                Color(shift(baseColor.red, 1.25), shift(baseColor.green, 0.85), shift(baseColor.blue, 0.85))
            }
        return Pair(addedColor, removedColor)
    }

    @Suppress("UNUSED_PARAMETER")
    fun generate(jbCefBrowser: JBCefBrowserBase): CefLoadHandlerAdapter {
        val isDarkTheme = EditorColorsManager.getInstance().isDarkEditor
        val isHighContrast =
            EditorColorsManager.getInstance().globalScheme.name.contains("High contrast", ignoreCase = true)

        return object : CefLoadHandlerAdapter() {
            override fun onLoadEnd(
                browser: CefBrowser,
                frame: CefFrame,
                httpStatusCode: Int,
            ) {
                if (frame.isMain) {
                    val baseColor = UIUtil.getTextFieldBackground() //TODO Replace with JBUI.CurrentTheme colors
                    val (addedColor, removedColor) = getCodeDiffColors(baseColor, isHighContrast) //TODO Replace with JBUI.CurrentTheme colors
                    val dataFlowColor = toCssHex(baseColor)
                    val editorColor = toCssHex(baseColor)

                    val textColor = toCssHex(JBUI.CurrentTheme.Tree.FOREGROUND)
                    val linkColor = toCssHex(JBUI.CurrentTheme.Link.Foreground.ENABLED)
                    val buttonColor = toCssHex(JBUI.CurrentTheme.Button.defaultButtonColorStart())
                    val borderColor = toCssHex(JBUI.CurrentTheme.CustomFrameDecorations.separatorForeground())
                    val labelColor = toCssHex(JBUI.CurrentTheme.Label.foreground())

                    val globalScheme = EditorColorsManager.getInstance().globalScheme
                    val tearLineColor = globalScheme.getColor(ColorKey.find("TEARLINE_COLOR")) //TODO Replace with JBUI.CurrentTheme colors
                    val tabItemHoverColor = globalScheme.getColor(ColorKey.find("INDENT_GUIDE")) //TODO Replace with JBUI.CurrentTheme colors
                    val codeTagBgColor = globalScheme.getColor(EditorColors.GUTTER_BACKGROUND)  ?: globalScheme.defaultBackground //TODO Replace with JBUI.CurrentTheme colors

                    val themeScript = """
                        (function(){
                        if (window.themeApplied) {
                            return;
                        }
                        window.themeApplied = true;
                        const style = getComputedStyle(document.documentElement);
                            const properties = {
                                '--text-color': "$textColor",
                                '--link-color': "$linkColor",
                                '--data-flow-body-color': "$dataFlowColor",
                                '--example-line-added-color': "${toCssHex(addedColor)}",
                                '--example-line-removed-color': "${toCssHex(removedColor)}",
                                '--tab-item-github-icon-color': "$textColor",
                                '--tab-item-hover-color': "${tabItemHoverColor?.let { toCssHex(it) }}",
                                '--scrollbar-thumb-color': "${tearLineColor?.let { toCssHex(it) }}",
                                '--tabs-bottom-color': "${tearLineColor?.let { toCssHex(it) }}",
                                '--border-color': "$borderColor",
                                '--editor-color': "$editorColor",
                                '--label-color': "'$labelColor'",
                                '--container-background-color': "${toCssHex(codeTagBgColor)}",
                                '--generated-ai-fix-button-background-color': "$buttonColor",
                                '--dark-button-border-default': "$borderColor",
                                '--dark-button-default': "$buttonColor",

                            };
                            for (let [property, value] of Object.entries(properties)) {
                                document.documentElement.style.setProperty(property, value);
                            }

                            // Add theme class to body
                            const isDarkTheme = $isDarkTheme;
                            const isHighContrast = $isHighContrast;
                            document.body.classList.add(isHighContrast ? 'high-contrast' : (isDarkTheme ? 'dark' : 'light'));
                        })();
                        """
                    browser.executeJavaScript(themeScript, browser.url, 0)
                }
            }
        }
    }
}
