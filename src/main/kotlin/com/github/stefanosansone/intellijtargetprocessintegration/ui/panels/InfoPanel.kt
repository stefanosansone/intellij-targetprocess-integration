package com.github.stefanosansone.intellijtargetprocessintegration.ui.panels

import ai.grazie.utils.capitalize
import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class InfoPanel(assignable: Assignables.Item) : JBScrollPane() {

    private var infoContentPanel = getInfoPanel(assignable)

    init {
        setViewportView(infoContentPanel)
        border = JBUI.Borders.empty()
    }

}
fun getInfoPanel(assignable: Assignables.Item) = panel {
    indent {
        row("Type:") {
            label(assignable.resourceType)
        }
        row("Project:") {
            label(assignable.project.name)
        }
        row("Feature:") {
            label(assignable.feature?.name ?: "")
        }
        row("Creator:") {
            label(assignable.creator.fullName)
        }
        row("Team Iteration:") {
            label(assignable.teamIteration?.name ?: "")
        }
        row("Creation date:") {
            label(parseDate(assignable.createDate))
        }
        row("Tags:") {
            label(assignable.tags)
        }
    }
}

fun parseDate(dateString: String): String {
    val regex = """/Date\((\d+)([+-]\d{4})\)/""".toRegex()
    val matchResult = regex.find(dateString)

    if (matchResult != null) {
        val (milliseconds, timezoneOffset) = matchResult.destructured
        val instant = Instant.fromEpochMilliseconds(milliseconds.toLong())
        val timezone = TimeZone.of("UTC$timezoneOffset")
        val localDateTime = instant.toLocalDateTime(timezone)

        val day = localDateTime.date.dayOfMonth.toString().padStart(2, '0')
        val month = localDateTime.date.month.name.substring(0, 3).lowercase().capitalize()
        val year = localDateTime.date.year.toString()

        return "$day-$month-$year"
    } else {
        return "Invalid date format"
    }
}