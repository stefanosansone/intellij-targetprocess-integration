package com.github.stefanosansone.intellijtargetprocessintegration.utils

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

fun markdownToHtml(markdown: String): String {
    val flavour = CommonMarkFlavourDescriptor()
    val parser = MarkdownParser(flavour)
    val parsedTree = parser.buildMarkdownTreeFromString(markdown)
    return HtmlGenerator(markdown, parsedTree, flavour).generateHtml()
}

fun convertImagesToTextLinks(htmlContent: String): String {
    val imgTagPattern = """<img\s+alt="([^"]+)"\s+src="([^"]+)"\s*/?>""".toRegex()
    return htmlContent.replace(imgTagPattern) { matchResult ->
        val imgName = matchResult.groups[1]?.value ?: "Image" // Use the alt text or a default
        val imgUrl = matchResult.groups[2]?.value ?: "#"
        """<a href="$imgUrl" target="_blank">$imgName</a>"""
    }
}

