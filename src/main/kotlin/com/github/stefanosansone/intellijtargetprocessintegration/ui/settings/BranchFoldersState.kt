package com.github.stefanosansone.intellijtargetprocessintegration.ui.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "BranchFoldersConfiguration", storages = [Storage("BranchFoldersConfiguration.xml")])
class BranchFoldersState : PersistentStateComponent<BranchFoldersState.FoldersState> {

    var foldersState: FoldersState = FoldersState()

    override fun getState(): FoldersState {
        return foldersState
    }

    override fun loadState(state: FoldersState) {
        XmlSerializerUtil.copyBean(state, this.foldersState)
    }

    fun addFolder(folderName: String) {
        if (folderName.isNotBlank() && !foldersState.folders.contains(folderName)) {
            foldersState.folders.add(folderName)
        }
    }

    fun removeFolder(folderName: String) {
        if (folderName.isNotBlank() && foldersState.folders.contains(folderName)) {
            foldersState.folders.remove(folderName)
        }
    }

    fun getFolders(): List<String> {
        return foldersState.folders.toList()
    }

    fun getLastSelectedFolder(): String {
        return foldersState.lastSelectedFolder
    }

    fun setLastSelectedFolder(folderName: String) {
        if (folderName.isNotBlank()) {
            foldersState.lastSelectedFolder = folderName
        }
    }

    companion object {
        val instance: BranchFoldersState
            get() = ApplicationManager.getApplication().getService(BranchFoldersState::class.java)
    }

    class FoldersState {
        // Initialize with "feature" as the default folder
        val folders: MutableList<String> = mutableListOf("feature")
        // Store the last selected folder
        var lastSelectedFolder: String = "feature"
    }
}
