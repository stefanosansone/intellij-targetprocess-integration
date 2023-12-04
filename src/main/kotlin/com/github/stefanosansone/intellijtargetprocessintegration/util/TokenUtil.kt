package com.github.stefanosansone.intellijtargetprocessintegration.util

fun isAccessTokenValid(token: String): Boolean {
    val regex = "^[a-zA-Z0-9]+$".toRegex()
    return token.length == 64 && regex.matches(token)
}