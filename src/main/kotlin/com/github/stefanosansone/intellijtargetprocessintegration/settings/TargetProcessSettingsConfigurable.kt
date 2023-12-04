package com.github.stefanosansone.intellijtargetprocessintegration.settings

import com.github.stefanosansone.intellijtargetprocessintegration.TargetProcessIntegrationBundle
import com.github.stefanosansone.intellijtargetprocessintegration.settings.ui.settingsPanel
import com.github.stefanosansone.intellijtargetprocessintegration.util.SETTINGS_ID
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel

internal class TargetProcessSettingsConfigurable : BoundSearchableConfigurable(
    TargetProcessIntegrationBundle.message("tps.settings.name"),
    TargetProcessIntegrationBundle.message("tps.settings.name"),
    SETTINGS_ID,
) {

    private val settingsState = TargetProcessSettingsState.instance.pluginState

    private var existingHostname = TargetProcessSettingsState.instance.state.targetProcessHostname
    private val panel = settingsPanel(existingHostname, settingsState)

    override fun createPanel(): DialogPanel {
        return panel
    }

    override fun apply() {
        if(this.panel.validateAll().isEmpty()){
            super.apply()
        }
    }
}