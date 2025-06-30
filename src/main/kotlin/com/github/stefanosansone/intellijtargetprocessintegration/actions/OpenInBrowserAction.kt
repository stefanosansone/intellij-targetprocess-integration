package com.github.stefanosansone.intellijtargetprocessintegration.actions

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.github.stefanosansone.intellijtargetprocessintegration.ui.settings.TargetProcessSettingsState
import com.github.stefanosansone.intellijtargetprocessintegration.utils.TargetProcessProjectService
import com.intellij.icons.AllIcons
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class OpenInBrowserAction : DumbAwareAction(
    "Open in Browser",
    "Open the selected assignable item in a web browser",
    AllIcons.General.Web
) {
    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            e.presentation.isEnabled = false
            return
        }

        val selectedItem = runBlocking {
            project.TargetProcessProjectService.selectedAssignableFlow.first()
        }

        e.presentation.isEnabled = selectedItem != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val selectedItem = runBlocking {
            project.TargetProcessProjectService.selectedAssignableFlow.first()
        }

        if (selectedItem == null) {
            Messages.showInfoMessage(project, "No assignable item selected. Please select an item in the My Items panel.", "Open in Browser")
            return
        }

        val hostname = TargetProcessSettingsState.instance.pluginState.targetProcessHostname
        if (hostname.isBlank()) {
            Messages.showErrorDialog(project, "TargetProcess hostname is not configured. Please check the settings.", "Open in Browser")
            return
        }

        try {
            val url = constructAssignableUrl(hostname, selectedItem)
            BrowserUtil.browse(url)
        } catch (ex: Exception) {
            thisLogger().warn("Error opening assignable in browser", ex)
            Messages.showErrorDialog(
                project,
                "Failed to open the assignable in browser: ${ex.message}",
                "Open in Browser Failed"
            )
        }
    }

    private fun constructAssignableUrl(hostname: String, item: Assignables.Item): String {
        val baseUrl = if (hostname.startsWith("http")) hostname else "https://$hostname"
        return "$baseUrl/entity/${item.id}"
    }
}