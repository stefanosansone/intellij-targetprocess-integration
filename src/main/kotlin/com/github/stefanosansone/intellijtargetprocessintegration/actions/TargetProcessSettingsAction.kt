package com.github.stefanosansone.intellijtargetprocessintegration.actions

import com.github.stefanosansone.intellijtargetprocessintegration.dialogs.SettingsDialogWrapper
import com.github.stefanosansone.intellijtargetprocessintegration.util.TP_TOKEN_STORE
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.github.stefanosansone.intellijtargetprocessintegration.util.createCredentialAttributes

class TargetProcessSettingsAction : AnAction() {
    override fun update(event: AnActionEvent) {
        // Using the event, evaluate the context,
        // and enable or disable the action.
    }

    override fun actionPerformed(event: AnActionEvent) {
        if (SettingsDialogWrapper().showAndGet()) {
            val credentialAttributes = createCredentialAttributes(TP_TOKEN_STORE)
            val credentials = Credentials(username, token)
            PasswordSafe.instance.set(credentialAttributes, credentials)
        }
    }
}