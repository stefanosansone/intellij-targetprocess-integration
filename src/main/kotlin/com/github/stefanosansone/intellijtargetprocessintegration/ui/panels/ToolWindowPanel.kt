package com.github.stefanosansone.intellijtargetprocessintegration.ui.panels

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.github.stefanosansone.intellijtargetprocessintegration.utils.TargetProcessProjectService
import com.intellij.openapi.project.Project
import com.intellij.ui.render.RenderingUtil
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.tree.TreeUtil
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

fun getAssignablesList(assignables: List<Assignables.Item>, showDetails: (Assignables.Item) -> Unit, project: Project? = null): Tree {
    val root = DefaultMutableTreeNode()
    val states = assignables.sortedBy { it.entityState.numericPriority }.map { it.entityState.name }.distinct()

    states.forEach { state ->
        val stateNode = DefaultMutableTreeNode(state)
        assignables.filter { it.entityState.name == state }.forEach { item ->
            val itemNode = DefaultMutableTreeNode(item.name)
            stateNode.add(itemNode)
        }
        root.add(stateNode)
    }

    val model = DefaultTreeModel(root)
    val tree = Tree(model).apply {
        selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        putClientProperty(RenderingUtil.ALWAYS_PAINT_SELECTION_AS_FOCUSED, true)
        isRootVisible = false
    }
    tree.addTreeSelectionListener {
        if (it.isAddedPath && it.path.parentPath.parentPath != null) {
            val selectedNode = it.path.lastPathComponent as? DefaultMutableTreeNode
            selectedNode?.let { node ->
                val item = assignables.first { it.name == node.userObject.toString() }
                showDetails(item)
                project?.TargetProcessProjectService?.setSelectedAssignable(item)
            }
        } else {
            project?.TargetProcessProjectService?.setSelectedAssignable(null)
        }
    }
    TreeUtil.installActions(tree)

    return tree
}
