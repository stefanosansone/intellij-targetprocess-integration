package com.github.stefanosansone.intellijtargetprocessintegration.ui.toolWindow


import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.github.stefanosansone.intellijtargetprocessintegration.services.AssignablesState
import com.github.stefanosansone.intellijtargetprocessintegration.ui.panels.DetailPanel
import com.github.stefanosansone.intellijtargetprocessintegration.ui.panels.InfoPanel
import com.github.stefanosansone.intellijtargetprocessintegration.ui.panels.getAssignablesList
import com.github.stefanosansone.intellijtargetprocessintegration.ui.settings.TargetProcessSettingsConfigurable
import com.github.stefanosansone.intellijtargetprocessintegration.utils.TargetProcessProjectService
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.OnePixelDivider
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.border.CustomLineBorder
import com.intellij.ui.components.JBPanelWithEmptyText
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI.Borders.empty
import com.intellij.util.ui.components.BorderLayoutPanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import javax.swing.border.Border
import javax.swing.border.CompoundBorder

class TargetProcessToolWindowFactory : ToolWindowFactory, DumbAware {

    private val scope = CoroutineScope(SupervisorJob())

    override fun init(toolWindow: ToolWindow) {
        val project = toolWindow.project
        val service = project.TargetProcessProjectService
        val bus = project.messageBus.connect()

        bus.subscribe(
            TargetProcessSettingsConfigurable.Util.SETTINGS_CHANGED,
            object : TargetProcessSettingsConfigurable.SettingsChangedListener {
                override fun settingsChanged() {
                    service.reloadClient()
                    ApplicationManager.getApplication().invokeLater {
                        updateToolWindowContent(toolWindow, AssignablesState.Loading)
                    }
                }
            })

        scope.launch {
            service.assignablesStateFlow.collect { state ->
                updateToolWindowContent(toolWindow, state)
            }
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        //  No direct content setup needed here
    }

    private fun updateToolWindowContent(toolWindow: ToolWindow, state: AssignablesState) {
        ApplicationManager.getApplication().invokeLater {
            toolWindow.contentManager.removeAllContents(true)

            when (state) {
                is AssignablesState.Loading -> displayMessagePanel(toolWindow, "Loading...")
                is AssignablesState.Success -> displayAssignables(toolWindow, state.items)
                is AssignablesState.MissingHostname -> displayMessagePanel(toolWindow, "Hostname missing!")
                is AssignablesState.MissingAccessToken -> displayMessagePanel(toolWindow, "Access token missing!")
                is AssignablesState.Error -> displayMessagePanel(toolWindow, "Error: ${state.error.message}")
            }
        }
    }

    private fun displayMessagePanel(toolWindow: ToolWindow, message: String) {
        toolWindow.contentManager.addContent(
            ContentFactory.getInstance().createContent(
                createInformativePanel(message), "My Items", false
            )
        )
    }

    private fun createInformativePanel(message: String): JBPanelWithEmptyText {
        val panel = JBPanelWithEmptyText()
        panel.emptyText.appendText(message)
        if (!message.contains("loading", ignoreCase = true)) {
            panel.emptyText.appendLine("Check TargetProcess settings.", SimpleTextAttributes.LINK_ATTRIBUTES, ActionUtil.createActionListener(
                "ShowPluginSettingsAction", panel, ActionPlaces.UNKNOWN
            ))
        }
        return panel
    }

    private fun displayAssignables(toolWindow: ToolWindow, assignables: List<Assignables.Item>) {
        toolWindow.contentManager.addContent(
            ContentFactory.getInstance().createContent(
                TargetProcessToolWindow(assignables).getContent(), "My Items", false
            )
        )
    }

    override fun shouldBeAvailable(project: Project) = true
}


class TargetProcessToolWindow(assignables: List<Assignables.Item>) {

    private val listPanel = JBScrollPane(getAssignablesList(assignables) { item ->
        showItemDetails(item)
    }).apply {
        val line: Border = CustomLineBorder(OnePixelDivider.BACKGROUND, 0, 1, 0, 0)
        border = CompoundBorder(line, empty())
    }

    private val detailPanel = DetailPanel()

    private val detailSplitter = OnePixelSplitter(false).apply {
        proportion = 0.70F
        val emptyDescriptionPanel = JBPanelWithEmptyText()
        emptyDescriptionPanel.emptyText.appendText("Select an entity to show description")
        firstComponent = emptyDescriptionPanel
        val emptyPropertiesPanel = JBPanelWithEmptyText()
        emptyPropertiesPanel.emptyText.appendText("Select an entity to show properties")
        secondComponent = emptyPropertiesPanel
    }

    private val assignablesSplitter = OnePixelSplitter(false).apply {
        setHonorComponentsMinimumSize(false)
        proportion = 0.25F
        firstComponent = listPanel
        secondComponent = detailSplitter
    }

    private fun showItemDetails(item: Assignables.Item) {
        val description = item.description
        description?.let {
            detailPanel.updateDescription(description)
            detailSplitter.firstComponent = detailPanel
            detailSplitter.secondComponent = InfoPanel(item)
            detailPanel.revalidate()
            detailPanel.repaint()
        } ?: run {
            val emptyTextPanel = JBPanelWithEmptyText()
            emptyTextPanel.emptyText.appendText("The selected entity has no description")
            detailSplitter.firstComponent = emptyTextPanel
        }
    }

    fun getContent(): BorderLayoutPanel {
        val component = BorderLayoutPanel()
        component.add(assignablesSplitter)
        val actionsManager = ActionManager.getInstance()
        val actionsGroup = actionsManager.getAction("TPIntegration.ActionGroup") as ActionGroup
        val actionToolbar = actionsManager.createActionToolbar(ActionPlaces.CONTEXT_TOOLBAR, actionsGroup, false)
        actionToolbar.targetComponent = component
        component.add(actionToolbar.component, BorderLayout.WEST)
        return component
    }
}
