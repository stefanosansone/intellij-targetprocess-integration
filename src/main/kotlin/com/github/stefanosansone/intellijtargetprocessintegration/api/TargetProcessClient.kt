package com.github.stefanosansone.intellijtargetprocessintegration.api

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables

interface TargetProcessClient {
    suspend fun assignables(): Assignables
}