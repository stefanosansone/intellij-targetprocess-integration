package com.github.stefanosansone.intellijtargetprocessintegration.services

import com.github.stefanosansone.intellijtargetprocessintegration.MyBundle
import com.github.stefanosansone.intellijtargetprocessintegration.configuration.PluginSettingsState
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project


@Service(Service.Level.PROJECT)
class TargetProcessIntegrationService(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun getAccessToken() = PluginSettingsState.instance.state.targetProcessAccessToken

}
