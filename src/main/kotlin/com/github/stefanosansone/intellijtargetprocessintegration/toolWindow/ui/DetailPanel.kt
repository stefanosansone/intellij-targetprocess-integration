package com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.ui

import com.github.stefanosansone.intellijtargetprocessintegration.util.isMarkdown
import com.github.stefanosansone.intellijtargetprocessintegration.util.markdownToHtml
import com.intellij.ui.components.DialogPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI

class DetailPanel : JBScrollPane() {

    private var detailContentPanel = DialogPanel()

    init {
        setViewportView(detailContentPanel)
        border = JBUI.Borders.empty()
    }

    fun updateDescription(description: String) {
        detailContentPanel = createContentPanel(description)
        setViewportView(detailContentPanel)
        revalidate()
        repaint()
    }

    private fun createContentPanel(description: String) = panel {
        val processedDescription = processDescription(description)
        indent {
            row {
                text(processedDescription).resizableColumn()
            }
        }
    }

    private fun processDescription(description: String): String {
        return (if (isMarkdown(description)) markdownToHtml(description) else description)
            .replace("<body>", "")
            .replace("</body>", "")
    }
}



