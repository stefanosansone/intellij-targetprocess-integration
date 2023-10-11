package com.github.stefanosansone.intellijtargetprocessintegration.api

import com.github.stefanosansone.intellijtargetprocessintegration.api.data.UserStory
import com.github.stefanosansone.intellijtargetprocessintegration.settings.TargetProcessSettingsState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

const val API_PATH = "/api/v1"
class KtorClient(): TargetProcessClient {
    private val apiHost = TargetProcessSettingsState.instance.pluginState.targetProcessHostname
    private val accessToken = TargetProcessSettingsState.instance.pluginState.targetProcessAccessToken

    private val client = HttpClient(CIO) {
        install(Logging)
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = apiHost
            }
            parameters {
                append("access_token", accessToken)
                append("format", "json")
            }
        }
    }

    override suspend fun userStories(): List<UserStory> {
        return client.get("${API_PATH}/UserStories/").body()
    }
}
