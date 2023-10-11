package com.github.stefanosansone.intellijtargetprocessintegration.api.data

data class User(
    val firstName: String,
    val fullName: String,
    val id: Int,
    val lastName: String,
    val login: String,
    val resourceType: String
)