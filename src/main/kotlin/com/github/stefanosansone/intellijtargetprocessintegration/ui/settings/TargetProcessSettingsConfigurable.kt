package com.github.stefanosansone.intellijtargetprocessintegration.ui.settings

import com.github.stefanosansone.intellijtargetprocessintegration.TargetProcessIntegrationBundle
import com.github.stefanosansone.intellijtargetprocessintegration.utils.SETTINGS_ID
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.util.messages.Topic

internal class TargetProcessSettingsConfigurable(private val project: Project) : BoundSearchableConfigurable(
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
        if (this.panel.validateAll().isEmpty()) {
            super.apply()
            project.messageBus.syncPublisher(Util.SETTINGS_CHANGED).settingsChanged()
        }
    }

    interface SettingsChangedListener {
        fun settingsChanged()
    }

    object Util {
        @JvmField
        @Topic.AppLevel
        val SETTINGS_CHANGED = Topic(SettingsChangedListener::class.java, Topic.BroadcastDirection.NONE)
    }
}