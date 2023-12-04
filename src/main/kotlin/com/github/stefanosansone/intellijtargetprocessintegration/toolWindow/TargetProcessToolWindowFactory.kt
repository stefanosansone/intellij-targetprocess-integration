package com.github.stefanosansone.intellijtargetprocessintegration.toolWindow

import com.github.stefanosansone.intellijtargetprocessintegration.api.KtorClient
import com.github.stefanosansone.intellijtargetprocessintegration.api.data.Assignables
import com.github.stefanosansone.intellijtargetprocessintegration.services.TargetProcessIntegrationService
import com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.ui.toolWindowDetailPanel
import com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.ui.toolWindowListPanel
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBSplitter
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBPanelWithEmptyText
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.runBlocking

class TargetProcessToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val disposable = Disposer.newDisposable("MyItems tab disposable")
        val service = toolWindow.project.service<TargetProcessIntegrationService>()
        Disposer.register(toolWindow.disposable, disposable)
        toolWindow.contentManager.removeAllContents(true)
        if (service.getAccessToken().isEmpty()) {
            noAccountPanel(disposable, toolWindow)
        } else {
            val myToolWindow = TargetProcessToolWindow(toolWindow)
            val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), "My Items", false)
            toolWindow.contentManager.addContent(content)
        }
    }

    private fun noAccountPanel(
        disposable: Disposable,
        toolWindow: ToolWindow
    ) = with(toolWindow.contentManager) {
        thisLogger().debug("No TargetProcess account configured")
        val emptyTextPanel = JBPanelWithEmptyText()
        emptyTextPanel.emptyText
            .appendText("Target Process access token not configured")
            .appendLine(
                "Go to TargetProcess Settings",
                SimpleTextAttributes.LINK_ATTRIBUTES,
                ActionUtil.createActionListener(
                    "ShowTargetProcessSettings",
                    emptyTextPanel,
                    ActionPlaces.UNKNOWN
                )
            )
        addContent(factory.createContent(emptyTextPanel, "My Items", false)
            .apply {
                isCloseable = false
                setDisposer(disposable)
            }
        )
    }

    override fun shouldBeAvailable(project: Project) = true

    class TargetProcessToolWindow(
        val toolWindow: ToolWindow
    ) {
        //private val service = toolWindow.project.service<TargetProcessIntegrationService>()

        private val listPanel = JBScrollPane(toolWindowListPanel { showItemDetails() }).apply {
            border = JBUI.Borders.empty()
        }

        private val detailPanel = JBScrollPane(toolWindowDetailPanel()).apply {
            border = JBUI.Borders.empty()
        }

        private val myItemsSplitter = JBSplitter(false).apply {
            setHonorComponentsMinimumSize(false)
            firstComponent = listPanel
        }

        private fun showItemDetails() {
            myItemsSplitter.secondComponent = detailPanel
        }

        fun getContent() = myItemsSplitter
    }

}

fun getStates(): List<Assignables.Item.EntityState> {
    val client = KtorClient()
    var assignables: List<Assignables.Item.EntityState>
    runBlocking {
        assignables = client.assignables().items.map { it.entityState }.distinct()
    }
    return assignables
}

fun getAssignables(): List<Assignables.Item> {
    val client = KtorClient()
    var assignables: List<Assignables.Item>
    runBlocking {
        assignables = client.assignables().items
    }
    return assignables
}