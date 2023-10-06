package com.github.stefanosansone.intellijtargetprocessintegration.actions

import com.github.stefanosansone.intellijtargetprocessintegration.settings.TargetProcessSettingsConfigurable
import com.github.stefanosansone.intellijtargetprocessintegration.settings.TargetProcessSettingsState
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil

class TargetProcessSettingsAction : AnAction() {

    private val targetProcessSettingsState
        get() = TargetProcessSettingsState.instance.state
    override fun update(event: AnActionEvent) {
        // Using the event, evaluate the context,
        // and enable or disable the action.
    }

    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let { ShowSettingsUtil.getInstance().showSettingsDialog(it, TargetProcessSettingsConfigurable()) }
/*        val settingsDialog = SettingsDialogWrapper()
        if (settingsDialog.showAndGet()) {
            targetProcessSettingsState.targetProcessAccessToken = settingsDialog.settingsModel.token
            targetProcessSettingsState.targetProcessHostname = settingsDialog.settingsModel.hostname
        }*/
    }
}