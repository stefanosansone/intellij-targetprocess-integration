package com.github.stefanosansone.intellijtargetprocessintegration.services

import com.github.stefanosansone.intellijtargetprocessintegration.TargetProcessIntegrationBundle
import com.github.stefanosansone.intellijtargetprocessintegration.ui.settings.TargetProcessSettingsState
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project


@Service(Service.Level.PROJECT)
class TargetProcessIntegrationService(project: Project) {

    init {
        thisLogger().info(TargetProcessIntegrationBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun getAccessToken() = TargetProcessSettingsState.instance.state.targetProcessAccessToken

}
