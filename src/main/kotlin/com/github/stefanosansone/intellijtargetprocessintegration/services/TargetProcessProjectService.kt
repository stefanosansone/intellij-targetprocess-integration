package com.github.stefanosansone.intellijtargetprocessintegration.services

import com.github.stefanosansone.intellijtargetprocessintegration.TargetProcessIntegrationBundle
import com.github.stefanosansone.intellijtargetprocessintegration.api.client.TargetProcessApiClient
import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.github.stefanosansone.intellijtargetprocessintegration.core.extensions.ProjectContext
import com.github.stefanosansone.intellijtargetprocessintegration.ui.settings.TargetProcessSettingsState
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Service(Service.Level.PROJECT)
class TargetProcessProjectService(override val project: Project) : ProjectContext, Disposable {

    override val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _assignablesStateFlow = MutableStateFlow<AssignablesState>(AssignablesState.Loading)
    val assignablesStateFlow: StateFlow<AssignablesState> = _assignablesStateFlow.asStateFlow()

    private val client = TargetProcessApiClient()

    init {
        thisLogger().info(TargetProcessIntegrationBundle.message("projectService", project.name))
        refreshAssignables()
    }

    fun refreshAssignables() {
        coroutineScope.launch {
            _assignablesStateFlow.value = AssignablesState.Loading
            try {
                val assignablesList = getAssignables()
                _assignablesStateFlow.value = AssignablesState.Success(assignablesList)
            } catch (e: Exception) {
                _assignablesStateFlow.value = AssignablesState.Error(e)
                thisLogger().warn(e)
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

sealed class AssignablesState {
    object Loading : AssignablesState()
    data class Success(val items: List<Assignables.Item>) : AssignablesState()
    data class Error(val error: Throwable) : AssignablesState()
}
