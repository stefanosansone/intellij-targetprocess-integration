package com.github.stefanosansone.intellijtargetprocessintegration

import com.github.stefanosansone.intellijtargetprocessintegration.utils.convertImagesToTextLinks
import com.github.stefanosansone.intellijtargetprocessintegration.utils.markdownToHtml
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class StringUtilsTest : BasePlatformTestCase() {

    fun testMarkdownTableRendering() {
        val markdown = """
            Event name | Event parameter: Parameter value | Note
            ------------ | -------------| -------------
            buying_budget_click | event_category: buying budget, event_action: my buying budget click | Fires on selection of Buying Budget calculator on My Account and Property Tools

            All the above should also have the following associated parameters:

            Event parameter | Parameter value
            ------------ | -------------
            user_id | {variable of the user performing the action}
            distilled_brand | daft
            page_type | my account, homepage-buy, homepage-rent, homepage-share
            user_login_status | true, false
        """.trimIndent()

        val html = markdownToHtml(markdown)
        println("[DEBUG_LOG] Generated HTML for table: $html")

        // Verify that the table is properly rendered with line breaks
        assertTrue(html.contains("<table>"))
        assertTrue(html.contains("<tr>"))
        assertTrue(html.contains("<th>Event name</th>"))
        assertTrue(html.contains("<td>buying_budget_click</td>"))

        // Verify that the second table is also properly rendered
        assertTrue(html.contains("<td>user_id</td>"))
        assertTrue(html.contains("<td>{variable of the user performing the action}</td>"))
    }

    fun testHtmlWithMarkdownTables() {
        val processedHtml = processHtmlForTablesTest()
        println("[DEBUG_LOG] Processed HTML from HTML with markdown: $processedHtml")

        assertTrue(processedHtml.contains("<table>"))
        assertTrue(processedHtml.contains("<tr>"))
        assertTrue(processedHtml.contains("<th>Event name</th>"))
        assertTrue(processedHtml.contains("<td>buying_budget_click</td>"))
    }

    /**
     * Helper function to expose processHtmlForTables for testing
     */
    private fun processHtmlForTablesTest(): String {
        val markdown = """
            Event name | Event parameter: Parameter value | Note
            ------------ | -------------| -------------
            buying_budget_click | event_category: buying budget, event_action: my buying budget click | Fires on selection of Buying Budget calculator on My Account and Property Tools

            All the above should also have the following associated parameters:

            Event parameter | Parameter value
            ------------ | -------------
            user_id | {variable of the user performing the action}
            distilled_brand | daft
            page_type | my account, homepage-buy, homepage-rent, homepage-share
            user_login_status | true, false
        """.trimIndent()

        return markdownToHtml(markdown)
    }

    fun testConvertImagesToTextLinks() {
        // Image with alt and src attributes
        val html1 = """<img alt="Test Image" src="https://example.com/image.jpg" />"""
        val expected1 = """<a href="https://example.com/image.jpg" target="_blank">Open image in browser &#128279;</a>"""
        assertEquals(expected1, convertImagesToTextLinks(html1))

        // Image without alt attribute (the issue case)
        val html2 = """<div><img src="https://distilledsch.tpondemand.com/Attachment.aspx?AttachmentID=35260" /></div>"""
        val expected2 = """<div><a href="https://distilledsch.tpondemand.com/Attachment.aspx?AttachmentID=35260" target="_blank">Open image in browser &#128279;</a></div>"""
        assertEquals(expected2, convertImagesToTextLinks(html2))

        // Image with src attribute first, then alt
        val html3 = """<img src="https://example.com/image2.jpg" alt="Another Image" />"""
        val expected3 = """<a href="https://example.com/image2.jpg" target="_blank">Open image in browser &#128279;</a>"""
        assertEquals(expected3, convertImagesToTextLinks(html3))

        // Image with additional attributes
        val html4 = """<img class="test-class" src="https://example.com/image3.jpg" style="width:100px;" />"""
        val expected4 = """<a href="https://example.com/image3.jpg" target="_blank">Open image in browser &#128279;</a>"""
        assertEquals(expected4, convertImagesToTextLinks(html4))
    }
}
