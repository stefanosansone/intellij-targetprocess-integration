package com.github.stefanosansone.intellijtargetprocessintegration.services

import com.github.stefanosansone.intellijtargetprocessintegration.TargetProcessIntegrationBundle
import com.github.stefanosansone.intellijtargetprocessintegration.api.client.TargetProcessApiClient
import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.github.stefanosansone.intellijtargetprocessintegration.core.extensions.AssignablesContext
import com.github.stefanosansone.intellijtargetprocessintegration.ui.settings.TargetProcessSettingsConfigurable
import com.github.stefanosansone.intellijtargetprocessintegration.ui.settings.TargetProcessSettingsState
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Service(Service.Level.PROJECT)
class TargetProcessProjectService(override val project: Project) : AssignablesContext, Disposable {

    override val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _assignablesStateFlow = MutableStateFlow<List<Assignables.Item>>(emptyList())
    val assignablesStateFlow: StateFlow<List<Assignables.Item>> = _assignablesStateFlow.asStateFlow()

    override val assignables = _assignablesStateFlow.asStateFlow().value

    private val client = TargetProcessApiClient()

    init {
        thisLogger().info(TargetProcessIntegrationBundle.message("projectService", project.name))
        refreshAssignables()
    }

    fun refreshAssignables() {
        coroutineScope.launch {
            try {
                val assignablesList = getAssignables()
                _assignablesStateFlow.value = assignablesList
            } catch (e: Exception) {
                showErrorDialogWithSettingsOption(e, project)
            }
        }
    }

    fun reloadClient() {
        client.reloadClientConfiguration()
        refreshAssignables()
    }

    private suspend fun getAssignables(): List<Assignables.Item> {
        return client.getAssignables().items
    }

    fun getAccessToken() = TargetProcessSettingsState.instance.state.targetProcessAccessToken

    override fun dispose() {
        coroutineScope.cancel()
    }
}


private fun showErrorDialogWithSettingsOption(e: Exception, project: Project) {
    // Constructing the message and showing the dialog should be done on the UI thread
    Messages.showErrorDialog(
            project,
            "Failed to fetch data from the specified address. Please check your settings. Error: ${e.localizedMessage}",
            "Error Fetching Data"
    )
    // Optionally, direct the user to the settings dialog
    ShowSettingsUtil.getInstance().showSettingsDialog(project, TargetProcessSettingsConfigurable::class.java)
}
