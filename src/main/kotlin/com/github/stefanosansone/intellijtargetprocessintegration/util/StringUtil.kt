package com.github.stefanosansone.intellijtargetprocessintegration.util

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

fun markdownToHtml(markdown: String): String {
    val flavour = CommonMarkFlavourDescriptor()
    val parser = MarkdownParser(flavour)
    val parsedTree = parser.buildMarkdownTreeFromString(markdown)
    return HtmlGenerator(markdown, parsedTree, flavour).generateHtml()
}