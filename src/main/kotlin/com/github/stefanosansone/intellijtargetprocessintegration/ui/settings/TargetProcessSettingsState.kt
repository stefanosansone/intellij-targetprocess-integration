package com.github.stefanosansone.intellijtargetprocessintegration.ui.settings

import com.github.stefanosansone.intellijtargetprocessintegration.util.EMPTY_STRING
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "TargetProcessStoredConfiguration", storages = [Storage("TargetProcessStoredConfiguration.xml")])
class TargetProcessSettingsState : PersistentStateComponent<TargetProcessSettingsState.PluginState> {

    var pluginState: PluginState = PluginState()

    override fun getState(): PluginState {
        return pluginState
    }

    override fun loadState(state: PluginState) {
        XmlSerializerUtil.copyBean(state, this.pluginState)
    }

    companion object {
        val instance: TargetProcessSettingsState
            get() = ApplicationManager.getApplication().getService(TargetProcessSettingsState::class.java)
    }

    class PluginState {
        var targetProcessAccessToken = EMPTY_STRING
        var targetProcessHostname = EMPTY_STRING
    }
}