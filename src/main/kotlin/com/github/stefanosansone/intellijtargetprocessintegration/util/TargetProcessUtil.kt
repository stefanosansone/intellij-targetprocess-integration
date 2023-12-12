package com.github.stefanosansone.intellijtargetprocessintegration.util

fun isMarkdown(description: String): Boolean {
    return description.trimStart().startsWith("<!--markdown-->")
}
