package com.github.stefanosansone.intellijtargetprocessintegration.actions

import com.github.stefanosansone.intellijtargetprocessintegration.configuration.PluginSettingsState
import com.github.stefanosansone.intellijtargetprocessintegration.dialogs.SettingsDialogWrapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class TargetProcessSettingsAction : AnAction() {

    private val pluginSettingsState
        get() = PluginSettingsState.instance.state
    override fun update(event: AnActionEvent) {
        // Using the event, evaluate the context,
        // and enable or disable the action.
    }

    override fun actionPerformed(event: AnActionEvent) {
        val settingsDialog = SettingsDialogWrapper()
        if (settingsDialog.showAndGet()) {
            pluginSettingsState.targetProcessAccessToken = settingsDialog.settingsModel.token
        }
    }
}