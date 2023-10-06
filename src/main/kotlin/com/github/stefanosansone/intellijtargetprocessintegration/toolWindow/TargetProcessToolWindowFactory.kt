package com.github.stefanosansone.intellijtargetprocessintegration.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.stefanosansone.intellijtargetprocessintegration.TargetProcessIntegrationBundle
import com.github.stefanosansone.intellijtargetprocessintegration.settings.TargetProcessSettingsState
import com.github.stefanosansone.intellijtargetprocessintegration.services.TargetProcessIntegrationService
import javax.swing.JButton


class TargetProcessToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = TargetProcessToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class TargetProcessToolWindow(toolWindow: ToolWindow) {

        //private val service = toolWindow.project.service<TargetProcessIntegrationService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel(TargetProcessIntegrationBundle.message("randomLabel", "?"))

            add(label)
            add(JButton(TargetProcessIntegrationBundle.message("shuffle")).apply {
                addActionListener {
                    label.text = TargetProcessIntegrationBundle.message("randomLabel", TargetProcessSettingsState.instance.state.targetProcessAccessToken)
                }
            })
        }
    }
}
