package com.github.stefanosansone.intellijtargetprocessintegration.api

import com.github.stefanosansone.intellijtargetprocessintegration.api.data.Assignables

object TargetProcessRepository {

    private val client = KtorClient()

    suspend fun getAssignables(): List<Assignables.Item> {
        return client.assignables().items
    }
}