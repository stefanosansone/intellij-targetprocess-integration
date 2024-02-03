package com.github.stefanosansone.intellijtargetprocessintegration.actions

import com.github.stefanosansone.intellijtargetprocessintegration.utils.TargetProcessProjectService
import com.intellij.icons.AllIcons
import com.intellij.ide.actions.RefreshAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager

class ReloadAssignablesAction : RefreshAction("Refresh", null, AllIcons.Actions.Refresh) {

    override fun update(e: AnActionEvent) {
        val context = e.project?.let { ToolWindowManager.getInstance(it).getToolWindow("Target Process") }
        e.presentation.isEnabled = context != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.TargetProcessProjectService?.refreshAssignables()
    }
}
