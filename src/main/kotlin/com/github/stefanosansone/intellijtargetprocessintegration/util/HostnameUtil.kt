package com.github.stefanosansone.intellijtargetprocessintegration.util

fun String.removeUrlPrefix(): String {
    val prefixes = listOf("https://", "http://")
    for (prefix in prefixes) {
        if (this.startsWith(prefix)) {
            return this.removePrefix(prefix)
        }
    }
    return this
}

fun String.isValidUrl(): Boolean {
    val regexPattern = """(www\.)?[a-zA-Z0-9@:%._+~#?&/=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%._+~#?&/=]*)""".toRegex()
    return regexPattern.matches(this)
}