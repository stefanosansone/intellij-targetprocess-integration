package com.github.stefanosansone.intellijtargetprocessintegration.ui.panels

import com.github.stefanosansone.intellijtargetprocessintegration.util.isMarkdown
import com.github.stefanosansone.intellijtargetprocessintegration.util.markdownToHtml
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.HtmlPanel
import com.intellij.util.ui.JBUI

class DetailPanel : JBScrollPane() {

    private var detailContentPanel = DescriptionPanel()

    init {
        setViewportView(detailContentPanel)
        border = JBUI.Borders.empty()
    }

    fun updateDescription(description: String) {
        detailContentPanel.updateDescription(description)
        setViewportView(detailContentPanel)
        revalidate()
        repaint()
    }

}

private class DescriptionPanel : HtmlPanel() {

    companion object {
        const val SIDE_BORDER = 14
        const val INTERNAL_BORDER = 10
        const val EXTERNAL_BORDER = 14
    }

    private var description: String? = null

    init {
        border = JBUI.Borders.empty(EXTERNAL_BORDER, SIDE_BORDER, INTERNAL_BORDER, 0)
        background = null
    }

    fun updateDescription(text: String) {
        description = (if (isMarkdown(text)) markdownToHtml(text) else text)
        update()
    }

    override fun getBody() = description ?: ""

    override fun update() {
        isVisible = description != null
        super.update()
    }
}