package com.github.stefanosansone.intellijtargetprocessintegration.api

import com.github.stefanosansone.intellijtargetprocessintegration.api.data.Assignables
import com.github.stefanosansone.intellijtargetprocessintegration.settings.TargetProcessSettingsState
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
class KtorClient: TargetProcessClient {
    private val apiHost = TargetProcessSettingsState.instance.pluginState.targetProcessHostname
    private val accessToken = TargetProcessSettingsState.instance.pluginState.targetProcessAccessToken

    private val client = HttpClient(CIO) {
        install(Resources)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
/*            logger = object : Logger {
                override fun log(message: String) {
                    thisLogger().warn("KTORRR : $message")
                }
            }*/
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

    override suspend fun assignables(): Assignables {
        return client.get {
            url("${API_PATH}/Assignables/")
            parameter("select","{ResourceType,Name,Id,EntityState,Effort,Tags,AssignedUser,Description}")
            parameter("take","1000")
            parameter("filter","?AssignedUser.Where(it is Me)")
        }.body()
    }
}
