package com.github.stefanosansone.intellijtargetprocessintegration.api

import com.github.stefanosansone.intellijtargetprocessintegration.api.data.UserStory

interface TargetProcessClient {
    suspend fun userStories(): List<UserStory>
}