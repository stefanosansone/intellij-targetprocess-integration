package com.github.stefanosansone.intellijtargetprocessintegration.utils

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

fun markdownToHtml(markdown: String): String {
    val flavour = CommonMarkFlavourDescriptor()
    val parser = MarkdownParser(flavour)
    val parsedTree = parser.buildMarkdownTreeFromString(markdown)
    val html = HtmlGenerator(markdown, parsedTree, flavour).generateHtml()

    return processHtmlForTables(processHtmlForImages(html))
}

/**
 * Process HTML to ensure images are properly rendered.
 * This function converts image tags to clickable links since the API doesn't allow downloading inline images.
 */
private fun processHtmlForImages(html: String): String {
    return convertImagesToTextLinks(html)
}

/**
 * Process HTML to properly format markdown tables that weren't converted by the markdown parser.
 * This function identifies markdown table patterns and converts them to proper HTML tables.
 */
private fun processHtmlForTables(html: String): String {
    val processedHtml = processEmbeddedMarkdownTables(html)

    val tablePattern = """<p>(.*?\|.*?)\n-+\s*\|\s*-+.*?\n(.*?)</p>""".toRegex(RegexOption.DOT_MATCHES_ALL)

    return processedHtml.replace(tablePattern) { matchResult ->
        val headerRow = matchResult.groupValues[1].trim()
        val bodyContent = matchResult.groupValues[2].trim()

        val headerColumns = headerRow.split("\\|".toRegex()).map { it.trim() }

        val bodyRows = bodyContent.split("\n").map { it.trim() }
            .filter { it.isNotEmpty() && it.contains("|") }

        buildHtmlTable(headerColumns, bodyRows)
    }
}

/**
 * Process HTML with embedded markdown tables.
 * This function specifically handles the case where markdown table syntax is embedded in HTML with <p> tags.
 */
private fun processEmbeddedMarkdownTables(html: String): String {
    val pattern = """<body>(?:<!--markdown-->)?(.*?)\|\s*(.*?)\|\s*(.*?)\n<p>-+\s*\|\s*-+\s*\|\s*-+\n(.*?)\|\s*(.*?)\|\s*(.*?)</p>""".toRegex(RegexOption.DOT_MATCHES_ALL)

    return html.replace(pattern) { matchResult ->
        val header1 = matchResult.groupValues[1].trim()
        val header2 = matchResult.groupValues[2].trim()
        val header3 = matchResult.groupValues[3].trim()
        val row1col1 = matchResult.groupValues[4].trim()
        val row1col2 = matchResult.groupValues[5].trim()
        val row1col3 = matchResult.groupValues[6].trim()

        """
        <table>
            <thead>
                <tr>
                    <th>$header1</th>
                    <th>$header2</th>
                    <th>$header3</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>$row1col1</td>
                    <td>$row1col2</td>
                    <td>$row1col3</td>
                </tr>
            </tbody>
        </table>
        """.trimIndent()
    }
}

/**
 * Build an HTML table from header columns and body rows.
 */
private fun buildHtmlTable(headerColumns: List<String>, bodyRows: List<String>): String {
    val sb = StringBuilder()
    sb.append("<table>")

    sb.append("<thead><tr>")
    for (column in headerColumns) {
        if (column.isNotEmpty()) {
            sb.append("<th>").append(column).append("</th>")
        }
    }
    sb.append("</tr></thead>")

    sb.append("<tbody>")
    for (row in bodyRows) {
        sb.append("<tr>")
        val columns = row.split("\\|".toRegex()).map { it.trim() }
        for (column in columns) {
            if (column.isNotEmpty()) {
                sb.append("<td>").append(column).append("</td>")
            }
        }
        sb.append("</tr>")
    }
    sb.append("</tbody>")

    sb.append("</table>")
    return sb.toString()
}

fun convertImagesToTextLinks(htmlContent: String): String {
    val imgTagPattern = """<img\s+(?:[^>]*?\s+)?src="([^"]+)"(?:\s+[^>]*?)?\s*/?>""".toRegex()
    return htmlContent.replace(imgTagPattern) { matchResult ->
        val imgUrl = matchResult.groups[1]?.value ?: "#"
        """<a href="$imgUrl" target="_blank">Open image in browser &#128279;</a>"""
    }
}
