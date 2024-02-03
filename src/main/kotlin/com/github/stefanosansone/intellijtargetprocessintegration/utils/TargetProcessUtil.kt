package com.github.stefanosansone.intellijtargetprocessintegration.utils

fun isMarkdown(description: String): Boolean {
    return description.trimStart().startsWith("<!--markdown-->")
}
