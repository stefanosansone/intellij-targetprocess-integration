package com.github.stefanosansone.intellijtargetprocessintegration.dialogs

import com.github.stefanosansone.intellijtargetprocessintegration.dialogs.ui.integrationSettingsUi
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel


class SettingsDialogWrapper : DialogWrapper(true) {
    init {
        title = "TargetProcess Settings"
        setSize(750,400)
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())
        dialogPanel.add(
            integrationSettingsUi()
        )
        return dialogPanel
    }
}