package com.github.stefanosansone.intellijtargetprocessintegration.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.stefanosansone.intellijtargetprocessintegration.MyBundle
import com.github.stefanosansone.intellijtargetprocessintegration.configuration.PluginSettingsState
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

        private val service = toolWindow.project.service<TargetProcessIntegrationService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel(MyBundle.message("randomLabel", "?"))

            add(label)
            add(JButton(MyBundle.message("shuffle")).apply {
                addActionListener {
                    label.text = MyBundle.message("randomLabel", PluginSettingsState.instance.state.targetProcessAccessToken)
                }
            })
        }
    }
}
