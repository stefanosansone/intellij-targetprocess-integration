package com.github.stefanosansone.intellijtargetprocessintegration.dialogs.ui

import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel

fun integrationSettingsUi(): DialogPanel {
    return panel {
        group("TargetProcess Access Token Setup") {
            row {
                text("A TargetProcess access token is required")
            }
            panel {
                row("Existing TargetProcess Access Token:") {
                    passwordField()
                        .align(AlignX.FILL)
                        .enabled(false)
                        .applyToComponent { text = "MzQ0OktVWjA1VkVsRDk3ckMvQk9BSDZaSElHd3N3eE1oYlp4enpoSitCSVlpMEU9" }
                }
            }
            panel {
                row("New TargetProcess Access Token:") {
                    textField()
                        .align(AlignX.FILL)
                        .comment("Note: this token will be stored securely on your local computer")
                }
            }
        }
    }
}