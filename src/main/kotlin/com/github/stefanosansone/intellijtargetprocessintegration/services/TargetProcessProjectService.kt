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
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Service(Service.Level.PROJECT)
class TargetProcessProjectService(override val project: Project) : ProjectContext, Disposable {

    override val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _assignablesStateFlow = MutableStateFlow<AssignablesState>(AssignablesState.Loading)
    val assignablesStateFlow: StateFlow<AssignablesState> = _assignablesStateFlow.asStateFlow()

    private val _selectedAssignableFlow = MutableStateFlow<Assignables.Item?>(null)
    val selectedAssignableFlow: StateFlow<Assignables.Item?> = _selectedAssignableFlow.asStateFlow()

    private val client = TargetProcessApiClient()

    init {
        thisLogger().info(TargetProcessIntegrationBundle.message("projectService", project.name))
        refreshAssignables()
    }

    fun refreshAssignables() {
        coroutineScope.launch {
            _assignablesStateFlow.value = AssignablesState.Loading

            val settings = TargetProcessSettingsState.instance.state
            if (settings.targetProcessHostname.isBlank()) {
                _assignablesStateFlow.value = AssignablesState.MissingHostname
                return@launch
            }
            if (settings.targetProcessAccessToken.isBlank()) {
                _assignablesStateFlow.value = AssignablesState.MissingAccessToken
                return@launch
            }

            try {
                val assignablesList = getAssignables()
                _assignablesStateFlow.value = AssignablesState.Success(assignablesList)
            } catch (e: ClientRequestException) {
                if (e.response.status == HttpStatusCode.Unauthorized) {
                    _assignablesStateFlow.value = AssignablesState.InvalidToken
                } else {
                    _assignablesStateFlow.value = AssignablesState.NetworkError(e)
                }
                thisLogger().warn("getAssignables: ClientRequestException", e)
            } catch (_: UnresolvedAddressException) {
                _assignablesStateFlow.value = AssignablesState.InvalidHostname
            } catch (e: Exception) {
                _assignablesStateFlow.value = AssignablesState.NetworkError(e)
                thisLogger().warn("getAssignables: General Exception", e)
            }
        }
    }

    fun reloadClient() {
        client.reloadClientConfiguration()
        refreshAssignables()
    }

    private suspend fun getAssignables(): List<Assignables.Item> {
        val response = client.getAssignables()

        if (response.status == HttpStatusCode.OK) {
            return (response.body() as Assignables).items
        } else {
            thisLogger().debug("Request failed: ${response.bodyAsText()}")
            throw ClientRequestException(response, "Request failed with status ${response.status}")
        }
    }

    fun getAccessToken() = TargetProcessSettingsState.instance.state.targetProcessAccessToken

    fun setSelectedAssignable(item: Assignables.Item?) {
        _selectedAssignableFlow.value = item
    }

    override fun dispose() {
        coroutineScope.cancel()
    }
}

sealed class AssignablesState {
    object Loading : AssignablesState()
    data class Success(val items: List<Assignables.Item>) : AssignablesState()
    object InvalidHostname : AssignablesState()
    object InvalidToken : AssignablesState()
    data class NetworkError(val error: Throwable) : AssignablesState() // Include the exception for logging
    object MissingHostname: AssignablesState()
    object MissingAccessToken: AssignablesState()
}
