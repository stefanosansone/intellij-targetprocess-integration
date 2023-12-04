package com.github.stefanosansone.intellijtargetprocessintegration.api

import com.github.stefanosansone.intellijtargetprocessintegration.api.data.Assignables

interface TargetProcessClient {
    suspend fun assignables(): Assignables
}