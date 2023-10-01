package com.github.stefanosansone.intellijtargetprocessintegration.dialogs

import com.github.stefanosansone.intellijtargetprocessintegration.dialogs.ui.integrationSettingsUi
import com.github.stefanosansone.intellijtargetprocessintegration.util.TP_TOKEN_STORE
import com.github.stefanosansone.intellijtargetprocessintegration.util.createCredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel


class SettingsDialogWrapper : DialogWrapper(true) {
    init {
        title = "TargetProcess Settings"
        isResizable = false
        setSize(750,300)
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())
        val credentialAttributes = createCredentialAttributes(TP_TOKEN_STORE)
        val token = PasswordSafe.instance.getPassword(credentialAttributes)

        dialogPanel.add(
            integrationSettingsUi(token)
        )
        return dialogPanel
    }

}