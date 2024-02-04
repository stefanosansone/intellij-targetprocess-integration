package com.github.stefanosansone.intellijtargetprocessintegration.api.client

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.github.stefanosansone.intellijtargetprocessintegration.ui.settings.TargetProcessSettingsState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

const val API_PATH = "api/v2"
class TargetProcessApiClient: TargetProcessApi {

    private var apiHost = TargetProcessSettingsState.instance.pluginState.targetProcessHostname
    private var accessToken = TargetProcessSettingsState.instance.pluginState.targetProcessAccessToken

    private var client = createClient()

    private fun createClient() = HttpClient(CIO) {
        install(Resources)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = apiHost
                parameters.append("access_token", accessToken)
            }
            parameters {
                append("format", "json")
            }
        }
    }

    fun reloadClientConfiguration() {
        apiHost = TargetProcessSettingsState.instance.pluginState.targetProcessHostname
        accessToken = TargetProcessSettingsState.instance.pluginState.targetProcessAccessToken
        client = createClient()
    }

    override suspend fun getAssignables(): Assignables {
        return client.get {
            url("$API_PATH/Assignables/")
            parameter("select","{ResourceType,Name,Id,EntityState,Effort,Tags,AssignedUser,Description,Project, Feature,Creator,Release,Iteration,TeamIteration,CreateDate}")
            parameter("take","1000")
            parameter("filter","?AssignedUser.Where(it is Me)")
        }.body()
    }
}