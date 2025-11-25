package com.github.stefanosansone.intellijtargetprocessintegration.ui.panels

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale.getDefault

class InfoPanel(assignable: Assignables.Item) : JBScrollPane() {

    private var infoContentPanel = getInfoPanel(assignable)

    init {
        setViewportView(infoContentPanel)
        border = JBUI.Borders.empty()
    }

}
fun getInfoPanel(assignable: Assignables.Item) = panel {
    indent {
        row("Id:") {
            label(assignable.id.toString())
        }
        row("Type:") {
            label(assignable.resourceType)
        }
        row("Project:") {
            label(assignable.project.name)
        }
        assignable.feature?.name.takeIf { !it.isNullOrEmpty() }?.let {
            row("Feature:") {
                label(it)
            }
        }
        row("Creator:") {
            label(assignable.creator.fullName)
        }
        assignable.teamIteration?.name.takeIf { !it.isNullOrEmpty() }?.let {
            row("Team Iteration:") {
                label(it)
            }
        }
        row("Creation date:") {
            label(parseDate(assignable.createDate))
        }
        assignable.tags.takeIf { it.isNotEmpty() }?.let {
            row("Tags:") {
                label(it)
            }
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
        val month = localDateTime.date.month.name.substring(0, 3).lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
        val year = localDateTime.date.year.toString()

        return "$day-$month-$year"
    } else {
        return "Invalid date format"
    }
}
