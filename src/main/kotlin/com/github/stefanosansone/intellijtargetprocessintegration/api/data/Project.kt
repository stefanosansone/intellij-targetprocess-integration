package com.github.stefanosansone.intellijtargetprocessintegration.api.data

data class Project(
    val Id: Int,
    val Name: String,
    val Process: Process,
    val ResourceType: String
)