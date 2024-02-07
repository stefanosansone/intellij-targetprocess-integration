package com.github.stefanosansone.intellijtargetprocessintegration.ui.toolWindow

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.github.stefanosansone.intellijtargetprocessintegration.services.AssignablesState
import com.github.stefanosansone.intellijtargetprocessintegration.ui.panels.DetailPanel
import com.github.stefanosansone.intellijtargetprocessintegration.ui.panels.InfoPanel
import com.github.stefanosansone.intellijtargetprocessintegration.ui.panels.getAssignablesList
import com.github.stefanosansone.intellijtargetprocessintegration.ui.settings.TargetProcessSettingsConfigurable
import com.github.stefanosansone.intellijtargetprocessintegration.utils.TargetProcessProjectService
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.OnePixelDivider
import com.intellij.openapi.util.Disposer
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
    private var assignables = mutableListOf<Assignables.Item>()

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
                ApplicationManager.getApplication().invokeLater {
                    updateToolWindowContent(toolWindow, state)
                }
            }
        }
    }


    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Nothing
    }

    private fun updateToolWindowContent(toolWindow: ToolWindow, state: AssignablesState) {
        val disposable = Disposer.newDisposable().also {
            Disposer.register(toolWindow.disposable, it)
        }
        toolWindow.contentManager.removeAllContents(true)

        when (state) {
            is AssignablesState.Loading -> messagePanel(disposable, toolWindow, "Loading data...", false)
            is AssignablesState.Success -> {
                assignables.addAll(state.items)
                val myToolWindow = TargetProcessToolWindow(assignables)
                val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), "My Items", false)
                toolWindow.contentManager.addContent(content)
            }

            is AssignablesState.Error -> messagePanel(
                disposable,
                toolWindow,
                state.error.localizedMessage ?: "Error fetching data. Check settings.",
                true
            )
        }
    }

    private fun messagePanel(
        disposable: Disposable, toolWindow: ToolWindow, message: String, showButtons: Boolean
    ) = with(toolWindow.contentManager) {
        thisLogger().debug("No TargetProcess account configured")
        val emptyTextPanel = JBPanelWithEmptyText()
        emptyTextPanel.emptyText.appendText(message)
        if (showButtons) {
            emptyTextPanel.emptyText.appendLine(
                "Go to TargetProcess settings", SimpleTextAttributes.LINK_ATTRIBUTES, ActionUtil.createActionListener(
                    "ShowTargetProcessSettings", emptyTextPanel, ActionPlaces.UNKNOWN
                )
            )
            emptyTextPanel.emptyText.appendLine(
                "Refresh", SimpleTextAttributes.LINK_ATTRIBUTES, ActionUtil.createActionListener(
                    "ReloadAssignablesAction", emptyTextPanel, ActionPlaces.UNKNOWN
                )
            )
        }
        addContent(factory.createContent(emptyTextPanel, "My Items", false).apply {
                isCloseable = false
                setDisposer(disposable)
            })
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
