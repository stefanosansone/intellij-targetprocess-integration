package com.github.stefanosansone.intellijtargetprocessintegration.dialogs

import com.github.stefanosansone.intellijtargetprocessintegration.configuration.PluginSettingsState
import com.github.stefanosansone.intellijtargetprocessintegration.dialogs.ui.settingsPanelUi
import com.github.stefanosansone.intellijtargetprocessintegration.util.EMPTY_STRING
import com.github.stefanosansone.intellijtargetprocessintegration.util.isAccessTokenValid
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import org.jetbrains.annotations.ApiStatus
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel


class SettingsDialogWrapper : DialogWrapper(true) {

    val settingsModel = SettingsModel()

    private var existingAccessToken = PluginSettingsState.instance.state.targetProcessAccessToken
    private val panel = settingsPanelUi(existingAccessToken, settingsModel)

    init {
        title = "TargetProcess Settings"
        isResizable = false
        setSize(750,300)
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())
        dialogPanel.add(panel)
        return dialogPanel
    }

    override fun doValidate(): ValidationInfo? {
        panel.apply()
        return if (settingsModel.token.isNotEmpty()) {
            null
        } else {
            ValidationInfo("Access token is required")
        }
    }

    @ApiStatus.Internal
    data class SettingsModel(
        var token: String = EMPTY_STRING
    )
}
