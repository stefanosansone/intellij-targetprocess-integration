package com.github.stefanosansone.intellijtargetprocessintegration.ui.panels

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI

class InfoPanel(assignable: Assignables.Item) : JBScrollPane() {

    private var infoContentPanel = getInfoPanel(assignable)

    init {
        setViewportView(infoContentPanel)
        border = JBUI.Borders.empty()
    }

}
fun getInfoPanel(assignable: Assignables.Item) = panel {
    indent {
        row("Type:") {
            label(assignable.resourceType)
        }
        row("Project:") {
            label(assignable.project.name)
        }
        row("Feature:") {
            label(assignable.feature?.name ?: "")
        }
        row("Creator:") {
            label(assignable.creator.fullName)
        }
        row("Team Iteration:") {
            label(assignable.teamIteration?.name ?: "")
        }
        row("Creation date:") {
            label(assignable.createDate)
        }
        row("Tags:") {
            label(assignable.tags)
        }
    }
}