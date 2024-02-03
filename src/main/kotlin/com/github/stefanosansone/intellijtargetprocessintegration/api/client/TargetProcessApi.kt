package com.github.stefanosansone.intellijtargetprocessintegration.api.client

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables

interface TargetProcessApi {
    suspend fun getAssignables(): Assignables
}