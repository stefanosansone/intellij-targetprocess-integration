package com.github.stefanosansone.intellijtargetprocessintegration.settings

import com.github.stefanosansone.intellijtargetprocessintegration.TargetProcessIntegrationBundle
import com.github.stefanosansone.intellijtargetprocessintegration.dialogs.SettingsDialogWrapper
import com.github.stefanosansone.intellijtargetprocessintegration.settings.ui.settingsPanelUi
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel

internal class TargetProcessSettingsConfigurable : BoundSearchableConfigurable(
    TargetProcessIntegrationBundle.message("tps.settings.name"),
    TargetProcessIntegrationBundle.message("tps.settings.name"),
"tpi.project.settings"
) {

    val settingsModel = SettingsDialogWrapper.SettingsModel()

    private var existingAccessToken = TargetProcessSettingsState.instance.state.targetProcessAccessToken
    private var existingHostname = TargetProcessSettingsState.instance.state.targetProcessHostname
    private val panel = settingsPanelUi(existingAccessToken, existingHostname, settingsModel)

    override fun createPanel(): DialogPanel {
        return panel
    }


}