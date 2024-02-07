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
                //assignables.addAll(state.items)
                assignables.addAll(getFakeAssignableList())
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

private fun getFakeAssignableList(): List<Assignables.Item> {
    val assignables = mutableListOf(
        Assignables.Item(
            assignedUser = Assignables.Item.AssignedUser(
                listOf(
                    Assignables.Item.AssignedUser.Item(
                        firstName = "John",
                        fullName = "John Doe",
                        id = 1,
                        kind = "Kind",
                        lastName = "Doe",
                        login = "john.doe@test.com",
                        resourceType = "GeneralUser"
                    )
                )
            ),
            effort = 1.0,
            entityState = Assignables.Item.EntityState(
                1,
                name = "To Do",
                numericPriority = 1.0,
                resourceType = "EntityState"
            ),
            id = 70432,
            name = "Refactor login system",
            description = "Improve the login system for better security.",
            resourceType = "UserStory",
            project = Assignables.Project(2, name = "Security Enhancements", resourceType = "Project"),
            feature = Assignables.Feature(87362, name = "Login", resourceType = "Example feature"),
            teamIteration = Assignables.TeamIteration(23441, name = "S2 Q1 2024", resourceType = "TeamIteration"),
            creator = Assignables.Creator(
                firstName = "Alice",
                fullName = "Alice Smith",
                id = 2,
                lastName = "Smith",
                login = "alice.smith@test.com",
                resourceType = "GeneralUser"
            ),
            createDate = "/Date(1688460741000+0200)/",
            tags = "security, refactor"
        ), Assignables.Item(
            assignedUser = Assignables.Item.AssignedUser(
                listOf(
                    Assignables.Item.AssignedUser.Item(
                        firstName = "Alice",
                        fullName = "Alice Smith",
                        id = 2,
                        kind = "Kind",
                        lastName = "Smith",
                        login = "alice.smith@test.com",
                        resourceType = "GeneralUser"
                    )
                )
            ),
            effort = 2.0,
            entityState = Assignables.Item.EntityState(
                2,
                name = "Planned",
                numericPriority = 2.0,
                resourceType = "EntityState"
            ),
            id = 70433,
            name = "Database optimization for reports",
            description = "Optimize the database queries for faster report generation.",
            resourceType = "UserStory",
            project = Assignables.Project(3, name = "Database Optimization", resourceType = "Project"),
            creator = Assignables.Creator(
                firstName = "Bob",
                fullName = "Bob Johnson",
                id = 3,
                lastName = "Johnson",
                login = "bob.johnson@test.com",
                resourceType = "GeneralUser"
            ),
            createDate = "2024-02-03",
            tags = "database, optimization",
            feature = Assignables.Feature(87362, name = "Login", resourceType = "Example feature"),
            teamIteration = Assignables.TeamIteration(23441, name = "S2 Q1 2024", resourceType = "TeamIteration")
        ), Assignables.Item(
            assignedUser = Assignables.Item.AssignedUser(
                listOf(
                    Assignables.Item.AssignedUser.Item(
                        firstName = "Charlie",
                        fullName = "Charlie Brown",
                        id = 4,
                        kind = "Kind",
                        lastName = "Brown",
                        login = "charlie.brown@test.com",
                        resourceType = "GeneralUser"
                    )
                )
            ),
            effort = 3.0,
            entityState = Assignables.Item.EntityState(
                3,
                name = "Reopen",
                numericPriority = 3.0,
                resourceType = "EntityState"
            ),
            id = 70434,
            name = "User feedback feature bugs",
            description = "Address bugs reported by users in the new feedback feature.",
            resourceType = "Bug",
            project = Assignables.Project(4, name = "User Feedback System", resourceType = "Project"),
            creator = Assignables.Creator(
                firstName = "Diana",
                fullName = "Diana Ross",
                id = 5,
                lastName = "Ross",
                login = "diana.ross@test.com",
                resourceType = "GeneralUser"
            ),
            createDate = "/Date(1688460741000+0200)/",
            tags = "feedback, bugs",
            feature = Assignables.Feature(87362, name = "Login", resourceType = "Example feature"),
            teamIteration = Assignables.TeamIteration(23441, name = "S2 Q1 2024", resourceType = "TeamIteration")
        ), Assignables.Item(
            assignedUser = Assignables.Item.AssignedUser(
                listOf(
                    Assignables.Item.AssignedUser.Item(
                        firstName = "Elena",
                        fullName = "Elena Torres",
                        id = 6,
                        kind = "Kind",
                        lastName = "Torres",
                        login = "elena.torres@test.com",
                        resourceType = "GeneralUser"
                    )
                )
            ),
            effort = 2.5,
            entityState = Assignables.Item.EntityState(
                4,
                name = "In Dev",
                numericPriority = 4.0,
                resourceType = "EntityState"
            ),
            id = 70435,
            name = "Implement OAuth 2.0",
            description = "Implement OAuth 2.0 for enhanced security.",
            resourceType = "UserStory",
            project = Assignables.Project(5, name = "Authentication System", resourceType = "Project"),
            creator = Assignables.Creator(
                firstName = "Frank",
                fullName = "Frank Miller",
                id = 7,
                lastName = "Miller",
                login = "frank.miller@test.com",
                resourceType = "GeneralUser"
            ),
            createDate = "/Date(1688460741000+0200)/",
            tags = "auth, security",
            feature = Assignables.Feature(87362, name = "Login", resourceType = "Example feature"),
            teamIteration = Assignables.TeamIteration(23441, name = "S2 Q1 2024", resourceType = "TeamIteration")
        ), Assignables.Item(
            assignedUser = Assignables.Item.AssignedUser(
                listOf(
                    Assignables.Item.AssignedUser.Item(
                        firstName = "Gina",
                        fullName = "Gina Hall",
                        id = 8,
                        kind = "Kind",
                        lastName = "Hall",
                        login = "gina.hall@test.com",
                        resourceType = "GeneralUser"
                    )
                )
            ),
            effort = 1.5,
            entityState = Assignables.Item.EntityState(
                1,
                name = "To Do",
                numericPriority = 1.0,
                resourceType = "EntityState"
            ),
            id = 70436,
            name = "UI Redesign for Dashboard",
            description = "Complete a UI redesign for the main dashboard to improve user experience.",
            resourceType = "UserStory",
            project = Assignables.Project(6, name = "UI Overhaul", resourceType = "Project"),
            creator = Assignables.Creator(
                firstName = "Harry",
                fullName = "Harry Potter",
                id = 9,
                lastName = "Potter",
                login = "harry.potter@test.com",
                resourceType = "GeneralUser"
            ),
            createDate = "/Date(1688547141000+0200)/",
            tags = "ui, redesign",
            feature = Assignables.Feature(87362, name = "Login", resourceType = "Example feature"),
            teamIteration = Assignables.TeamIteration(23441, name = "S2 Q1 2024", resourceType = "TeamIteration")
        )
    )

    return assignables
}
