package com.github.stefanosansone.intellijtargetprocessintegration.actions

import com.github.stefanosansone.intellijtargetprocessintegration.util.SETTINGS_ID
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAwareAction

class TargetProcessSettingsAction : DumbAwareAction() {
    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let { ShowSettingsUtil.getInstance().showSettingsDialog(it, SETTINGS_ID) }
    }
}
