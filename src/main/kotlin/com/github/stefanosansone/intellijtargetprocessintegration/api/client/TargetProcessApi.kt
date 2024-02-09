package com.github.stefanosansone.intellijtargetprocessintegration.api.client

import io.ktor.client.statement.*

interface TargetProcessApi {
    suspend fun getAssignables(): HttpResponse
}