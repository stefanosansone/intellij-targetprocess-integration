package com.github.stefanosansone.intellijtargetprocessintegration.listeners

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.wm.IdeFrame

internal class TPIActivationListener : ApplicationActivationListener {
    override fun applicationActivated(ideFrame: IdeFrame) {}
}
