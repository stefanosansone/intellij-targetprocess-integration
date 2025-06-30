package com.github.stefanosansone.intellijtargetprocessintegration.actions

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.github.stefanosansone.intellijtargetprocessintegration.ui.settings.BranchFoldersState
import com.github.stefanosansone.intellijtargetprocessintegration.utils.TargetProcessProjectService
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.*

class CreateBranchAction : DumbAwareAction(
    "Create Branch",
    "Create a new branch using the name of the selected assignable item",
    AllIcons.Vcs.Branch
) {
    companion object {
        fun addFolder(folder: String) {
            BranchFoldersState.instance.addFolder(folder)
        }

        fun removeFolder(folder: String) {
            if (folder != "No folder") {
                BranchFoldersState.instance.removeFolder(folder)
            }
        }

        fun getFolders(): List<String> {
            return BranchFoldersState.instance.getFolders()
        }

        fun getLastSelectedFolder(): String {
            return BranchFoldersState.instance.getLastSelectedFolder()
        }

        fun setLastSelectedFolder(folder: String) {
            BranchFoldersState.instance.setLastSelectedFolder(folder)
        }
    }
    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            e.presentation.isEnabled = false
            return
        }

        val selectedItem = runBlocking {
            project.TargetProcessProjectService.selectedAssignableFlow.first()
        }

        e.presentation.isEnabled = selectedItem != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val selectedItem = runBlocking {
            project.TargetProcessProjectService.selectedAssignableFlow.first()
        }

        if (selectedItem == null) {
            Messages.showInfoMessage(project, "No assignable item selected. Please select an item in the My Items panel.", "Create Branch")
            return
        }

        val branchName = createBranchName(selectedItem)

        val dialog = BranchNameDialog(project, branchName)
        if (dialog.showAndGet()) {
            val editedBranchName = dialog.getBranchName()
            createGitBranch(project, editedBranchName)
        }
    }

    private class BranchNameDialog(project: Project, initialBranchName: String) : DialogWrapper(project) {
        private val textField = JTextField(extractBranchNameWithoutFolder(initialBranchName), 40)
        private val folderComboBox = ComboBox<String>()

        init {
            title = "Create Branch"
            setupFolderComboBox(initialBranchName)
            init()
        }

        private fun setupFolderComboBox(initialBranchName: String) {
            folderComboBox.addItem("No folder")

            val folders = getFolders()
            for (folder in folders) {
                folderComboBox.addItem(folder)
            }

            val initialFolder = extractFolderFromBranchName(initialBranchName)
            if (initialFolder != null && folders.contains(initialFolder)) {
                folderComboBox.selectedItem = initialFolder
            } else {
                val lastSelectedFolder = getLastSelectedFolder()
                if (lastSelectedFolder != "No folder" && folders.contains(lastSelectedFolder)) {
                    folderComboBox.selectedItem = lastSelectedFolder
                } else {
                    folderComboBox.selectedIndex = 0
                }
            }

            folderComboBox.addActionListener {
                val selectedFolder = folderComboBox.selectedItem as String
                if (selectedFolder != "No folder") {
                    setLastSelectedFolder(selectedFolder)
                }
            }
        }

        private fun extractFolderFromBranchName(branchName: String): String? {
            return if (branchName.contains("/")) {
                branchName.substring(0, branchName.indexOf("/"))
            } else {
                null
            }
        }

        private fun extractBranchNameWithoutFolder(branchName: String): String {
            return if (branchName.contains("/")) {
                branchName.substring(branchName.indexOf("/") + 1)
            } else {
                branchName
            }
        }

        override fun createCenterPanel(): JComponent {
            val panel = JPanel(BorderLayout())

            val formPanel = JPanel()
            formPanel.layout = BoxLayout(formPanel, BoxLayout.Y_AXIS)

            val folderLabelPanel = JPanel(BorderLayout())
            folderLabelPanel.add(JLabel("Folder:"), BorderLayout.WEST)

            val folderPanel = JPanel(BorderLayout())

            val folderControlsPanel = JPanel()
            folderControlsPanel.layout = BoxLayout(folderControlsPanel, BoxLayout.X_AXIS)
            folderControlsPanel.add(folderComboBox)
            folderControlsPanel.add(Box.createHorizontalStrut(5))

            val addFolderButton = JButton("Add Folder")
            addFolderButton.addActionListener {
                val folderName = Messages.showInputDialog(
                    "Enter new folder name:",
                    "Add New Folder",
                    Messages.getQuestionIcon()
                )
                if (!folderName.isNullOrBlank()) {
                    addFolder(folderName)
                    folderComboBox.addItem(folderName)
                    folderComboBox.selectedItem = folderName
                }
            }
            folderControlsPanel.add(addFolderButton)

            val deleteFolderButton = JButton("Remove Folder")
            deleteFolderButton.addActionListener {
                val selectedFolder = folderComboBox.selectedItem as String
                if (selectedFolder != "No folder") {
                    val confirmed = Messages.showYesNoDialog(
                        "Are you sure you want to delete the folder '$selectedFolder'?",
                        "Delete Folder",
                        Messages.getQuestionIcon()
                    )
                    if (confirmed == Messages.YES) {
                        removeFolder(selectedFolder)
                        folderComboBox.removeItem(selectedFolder)
                        folderComboBox.selectedIndex = 0
                    }
                } else {
                    Messages.showInfoMessage(
                        "Cannot delete the 'No folder' option.",
                        "Delete Folder"
                    )
                }
            }
            folderControlsPanel.add(deleteFolderButton)

            folderPanel.add(folderControlsPanel, BorderLayout.CENTER)

            val labelPanel = JPanel(BorderLayout())
            labelPanel.add(JLabel("Branch name:"), BorderLayout.WEST)

            val fieldPanel = JPanel(BorderLayout())
            textField.selectAll()
            fieldPanel.add(textField, BorderLayout.CENTER)

            val commentPanel = JPanel(BorderLayout())
            commentPanel.add(JLabel("Edit the branch name or leave it as is."), BorderLayout.WEST)

            formPanel.add(folderLabelPanel)
            formPanel.add(folderPanel)
            formPanel.add(Box.createVerticalStrut(10))
            formPanel.add(labelPanel)
            formPanel.add(fieldPanel)
            formPanel.add(Box.createVerticalStrut(10))
            formPanel.add(commentPanel)

            panel.add(formPanel, BorderLayout.CENTER)
            panel.preferredSize = Dimension(450, 150)

            return panel
        }

        fun getBranchName(): String {
            val branchNameText = textField.text
            val selectedItem = folderComboBox.selectedItem as String

            return if (selectedItem == "No folder") {
                branchNameText
            } else {
                "$selectedItem/$branchNameText"
            }
        }
    }

    private fun createBranchName(item: Assignables.Item): String {
        val sanitizedName = item.name
            .replace("[^\\w\\s-]".toRegex(), "")
            .replace("\\s+".toRegex(), "-")
            .lowercase()

        return "TP-${item.id}-$sanitizedName"
    }

    private fun createGitBranch(project: Project, branchName: String) {
        try {
            val processBuilder = ProcessBuilder("git", "checkout", "-b", branchName)
            processBuilder.directory(File(project.basePath ?: ""))

            val process = processBuilder.start()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                Messages.showInfoMessage(
                    project,
                    "Branch '$branchName' has been created successfully.",
                    "Branch Created"
                )
            } else {
                val errorStream = process.errorStream.bufferedReader().readText()
                Messages.showErrorDialog(
                    project,
                    "Failed to create branch '$branchName': $errorStream",
                    "Branch Creation Failed"
                )
            }
        } catch (e: Exception) {
            thisLogger().warn("Error creating Git branch", e)
            Messages.showErrorDialog(
                project,
                "Failed to create branch '$branchName': ${e.message}",
                "Branch Creation Failed"
            )
        }
    }
}
