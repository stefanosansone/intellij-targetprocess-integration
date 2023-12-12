package com.github.stefanosansone.intellijtargetprocessintegration.settings.ui

import com.github.stefanosansone.intellijtargetprocessintegration.TargetProcessIntegrationBundle
import com.github.stefanosansone.intellijtargetprocessintegration.settings.TargetProcessSettingsState
import com.github.stefanosansone.intellijtargetprocessintegration.util.isAccessTokenValid
import com.github.stefanosansone.intellijtargetprocessintegration.util.isValidUrl
import com.github.stefanosansone.intellijtargetprocessintegration.util.removeUrlPrefix
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
                            it.text = it.text.removeUrlPrefix()
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
                row("TargetProcess access token:") {
                    passwordField()
                        .bindText(settingsState::targetProcessAccessToken)
                        .align(AlignX.FILL)
                        .resizableColumn()
                        .resizableColumn()
                        .validationOnApply {
                            when {
                                !isAccessTokenValid(it.text) && it.text.isNotEmpty() -> error("Access Token format not valid")
                                else -> null
                            }
                        }
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
