package com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.ui

import com.intellij.ui.components.DialogPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI

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
    indent {
        row {
            text(description).resizableColumn()
        }
    }
}

