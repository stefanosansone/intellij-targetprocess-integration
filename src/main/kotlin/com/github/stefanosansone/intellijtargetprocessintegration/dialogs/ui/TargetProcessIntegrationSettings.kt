package com.github.stefanosansone.intellijtargetprocessintegration.dialogs.ui

import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.panel

fun integrationSettingsUi(): DialogPanel {
    return panel {
        group("TargetProcess Access Token Setup") {
            row {
                text("A TargetProcess access token is required to set the IntelliJ integration plugin.<br>If you wish to create a new one, follow these steps:")
            }.bottomGap(BottomGap.SMALL)
            row { text("1. Login to your TargetProcess account.") }
            row { text("2. Click your account avatar in the top right area of the windows.") }
            row { text("3. Select your account name from the drop-down menu under your account avatar.") }
            row { text("4. Select the 'Access Tokens' tab.") }
            row { text("5. Click the '+ Add token' button.") }
            row { text("6. In the text field, type a name to identify this token.") }
            row { text("7. Press 'Create'. This will generate a new token.") }
            row { text("8. Copy and paste your new TargetProcess Access Token in the box below.") }.bottomGap(BottomGap.MEDIUM)
            panel {
                row("Existing TargetProcess Access Token:") {
                    passwordField()
                        .align(AlignX.FILL)
                        .enabled(false)
                        .applyToComponent { text = "test" }
                }
            }
            panel {
                row("New TargetProcess Access Token:") {
                    textField()
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