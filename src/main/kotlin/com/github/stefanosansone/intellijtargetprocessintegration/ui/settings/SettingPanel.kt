package com.github.stefanosansone.intellijtargetprocessintegration.ui.settings

import com.github.stefanosansone.intellijtargetprocessintegration.TargetProcessIntegrationBundle
import com.github.stefanosansone.intellijtargetprocessintegration.utils.*
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*

fun settingsPanel(
    existingHostname: String,
    settingsState: TargetProcessSettingsState.PluginState
): DialogPanel {
    return panel {
        group(TargetProcessIntegrationBundle.message("tps.settings.configuration")) {
            panel {
                row {
                    textField()
                        .label(TargetProcessIntegrationBundle.message("tps.settings.configuration.hostname"))
                        .bindText(settingsState::targetProcessHostname)
                        .text(existingHostname)
                        .validationOnApply {
                            it.text = it.text.formatUrl()
                            when {
                                it.text.isNotEmpty() && !it.text.isValidUrl() -> error(TargetProcessIntegrationBundle.message("tps.settings.configuration.hostname.error.invalid"))
                                else -> null
                            }
                        }
                        .columns(COLUMNS_LARGE)
                        .align(AlignX.LEFT)
                        .comment("<font color=\"#DFE1E5\"><i>Example: https://myaccount.tpondemand.com</i></font>")
                }
            }
        }
        group(TargetProcessIntegrationBundle.message("tps.settings.token")) {
            row {
                text(TargetProcessIntegrationBundle.message("tps.settings.token.steps.description"))
            }.bottomGap(BottomGap.SMALL)
            row { text(TargetProcessIntegrationBundle.message("tps.settings.token.steps.one")) }
            row { text(TargetProcessIntegrationBundle.message("tps.settings.token.steps.two")) }
            row { text(TargetProcessIntegrationBundle.message("tps.settings.token.steps.three")) }
            row { text(TargetProcessIntegrationBundle.message("tps.settings.token.steps.four")) }
            row { text("5. Click the '+ Add token' button.") }
            row { text("6. In the text field, type a name to identify this token.") }
            row { text("7. Press 'Create'. This will generate a new token.") }
            row { text("8. Copy and paste your new TargetProcess Access Token in the box below.") }.bottomGap(BottomGap.MEDIUM)
            panel {
                row("TargetProcess access token:") {
                    passwordField()
                        .bindText(settingsState::targetProcessAccessToken)
                        .columns(COLUMNS_LARGE)
                        .align(AlignX.LEFT)
                        .validationOnApply {
                            when {
                                !isAccessTokenValid(it.password.concatToString()) && it.password.concatToString().isNotEmpty() -> error("Access Token format not valid")
                                else -> null
                            }
                        }
                        .comment("<font color=\"#DFE1E5\"><i>Note: this token will be stored securely on your local computer</i></font>")
                }
            }
        }
        group(TargetProcessIntegrationBundle.message("tps.settings.feedback.title")) {
            row {
                browserLink(TargetProcessIntegrationBundle.message("tps.settings.feedback.issue"), GITHUB_ISSUES_URL)
            }
            row {
                browserLink(TargetProcessIntegrationBundle.message("tps.settings.feedback.send"), FEEDBACK_MAIL)
            }
        }
    }
}
