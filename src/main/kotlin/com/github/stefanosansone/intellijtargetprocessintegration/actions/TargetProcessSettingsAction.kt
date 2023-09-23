package com.github.stefanosansone.intellijtargetprocessintegration.actions

import com.github.stefanosansone.intellijtargetprocessintegration.dialogs.SettingsDialogWrapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent


class TargetProcessSettingsAction : AnAction() {
    override fun update(event: AnActionEvent) {
        // Using the event, evaluate the context,
        // and enable or disable the action.
    }

    override fun actionPerformed(event: AnActionEvent) {
        SettingsDialogWrapper().showAndGet()
    }
}