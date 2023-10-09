package com.github.stefanosansone.intellijtargetprocessintegration.settings.ui

import com.github.stefanosansone.intellijtargetprocessintegration.TargetProcessIntegrationBundle
import com.github.stefanosansone.intellijtargetprocessintegration.settings.TargetProcessSettingsState
import com.github.stefanosansone.intellijtargetprocessintegration.util.isAccessTokenValid
import com.github.stefanosansone.intellijtargetprocessintegration.util.isValidUrl
import com.github.stefanosansone.intellijtargetprocessintegration.util.withScheme
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*

fun settingsPanel(
    existingToken: String,
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
                            it.text = it.text.withScheme()
                            when {
                                it.text.isNotEmpty() && !it.text.isValidUrl() -> error(TargetProcessIntegrationBundle.message("tps.settings.configuration.hostname.error.invalid"))
                                else -> null
                            }
                        }
                        .align(AlignX.FILL)
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
            row { text("3. Select your account name from the drop-down menu under your account avatar.") }
            row { text("4. Select the 'Access Tokens' tab.") }
            row { text("5. Click the '+ Add token' button.") }
            row { text("6. In the text field, type a name to identify this token.") }
            row { text("7. Press 'Create'. This will generate a new token.") }
            row { text("8. Copy and paste your new TargetProcess Access Token in the box below.") }.bottomGap(BottomGap.MEDIUM)
            panel {
                val action = object : DumbAwareAction("Delete Token", "Action description", AllIcons.Actions.Cancel) {
                    override fun actionPerformed(e: AnActionEvent) {
                    }
                }
                row("Existing TargetProcess access token:") {
                    passwordField()
                        .text(existingToken)
                        .align(AlignX.FILL)
                        .resizableColumn()
                        .enabled(false)
                        .resizableColumn()
                    actionButton(action)
                }
            }
            panel {
                row {
                    textField()
                        .label("New TargetProcess access token:")
                        .bindText(settingsState::targetProcessAccessToken)
                        .validationOnApply {
                            when {
                                isAccessTokenValid(it.text) -> error("Access Token format not valid")
                                else -> null
                            }
                        }
                        .align(AlignX.FILL)
                        .comment("<font color=\"#DFE1E5\"><i>Note: this token will be stored securely on your local computer</i></font>")
                }
            }
        }
        group("Feedback") {
            row {
                browserLink("Send feedback about TargetProcess Integration", "https://stefanosansone.dev")
            }
        }
    }
}
