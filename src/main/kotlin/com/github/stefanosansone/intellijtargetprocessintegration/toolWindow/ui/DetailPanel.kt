package com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.ui

import com.github.stefanosansone.intellijtargetprocessintegration.util.isMarkdown
import com.intellij.ui.components.DialogPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

class DetailPanel : JBScrollPane() {

    private var detailPanel = DialogPanel()

    init {
        setViewportView(detailPanel)
        border = JBUI.Borders.empty()
    }

    fun updateDescription(description: String) {
        detailPanel = buildDetailPanel(description)
        setViewportView(detailPanel)
        revalidate()
        repaint()
    }
}

fun buildDetailPanel(description: String) = panel {
    val desc = (if (isMarkdown(description)) parseMarkdown(description) else description)
        .replace("<body>", "")
        .replace("</body>", "")
    indent {
        row {
            text(desc).resizableColumn()
        }
    }
}

fun parseMarkdown(text: String): String {
    val stringToParse = text.removePrefix("<!--markdown-->")
    val flavour = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(stringToParse)
    return HtmlGenerator(stringToParse, parsedTree, flavour).generateHtml()
}

