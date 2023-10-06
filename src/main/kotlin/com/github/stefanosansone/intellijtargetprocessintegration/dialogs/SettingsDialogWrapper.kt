package com.github.stefanosansone.intellijtargetprocessintegration.dialogs

import com.github.stefanosansone.intellijtargetprocessintegration.settings.TargetProcessSettingsState
import com.github.stefanosansone.intellijtargetprocessintegration.settings.ui.settingsPanelUi
import com.github.stefanosansone.intellijtargetprocessintegration.util.EMPTY_STRING
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import org.jetbrains.annotations.ApiStatus


class SettingsDialogWrapper : DialogWrapper(true) {

    val settingsModel = SettingsModel()

    private var existingAccessToken = TargetProcessSettingsState.instance.state.targetProcessAccessToken
    private var existingHostname = TargetProcessSettingsState.instance.state.targetProcessHostname
    //private val panel = settingsPanelUi(existingAccessToken, existingHostname, settingsModel)

    init {
        title = "TargetProcess Settings"
        isResizable = false
        setSize(750,300)
        init()
    }

    override fun createCenterPanel() = settingsPanelUi(existingAccessToken, existingHostname, settingsModel)

    override fun doValidate(): ValidationInfo? {
        return null
/*        panel.apply()
        return if (settingsModel.token.isEmpty()) {
            ValidationInfo("TargetProcess Access token is required")
        } else if (settingsModel.hostname.isEmpty()) {
            ValidationInfo("TargetProcess URL is required")
        } else {
            null
        }*/
    }

    @ApiStatus.Internal
    data class SettingsModel(
        var token: String = EMPTY_STRING,
        var hostname: String = EMPTY_STRING
    )
}
