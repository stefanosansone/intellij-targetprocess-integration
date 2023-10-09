package com.github.stefanosansone.intellijtargetprocessintegration.util

fun String.withScheme(): String {
    return if (startsWith("https://") || startsWith("http://")) {
        this
    } else if (isNotEmpty()) {
        "https://$this"
    } else {
        this
    }
}

fun String.isValidUrl(): Boolean {
    val regexPattern = """((http|https)://)(www\.)?[a-zA-Z0-9@:%._\+~#?&//=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%._\+~#?&//=]*)""".toRegex()
    return regexPattern.matches(this)
}