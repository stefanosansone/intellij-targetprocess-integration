package com.github.stefanosansone.intellijtargetprocessintegration.api

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables

object TargetProcessRepository {

    private val client = KtorClient()

    suspend fun getAssignables(): List<Assignables.Item> {
        return client.assignables().items
    }
}