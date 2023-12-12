package com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.ui

import com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.getAssignables
import com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.getStates
import com.intellij.ui.render.RenderingUtil
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.tree.TreeUtil
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

fun getAssignablesList(onSelectNode: (String) -> Unit): Tree {
    val root = DefaultMutableTreeNode()
    val states = getStates().sortedBy { it.numericPriority }.map { it.name }.distinct()
    states.forEach { state ->
        val stateNode = DefaultMutableTreeNode(state)
        getAssignables().forEach { item ->
            if (item.entityState.name == state) {
                val itemNode = DefaultMutableTreeNode(
                    item.name
                )
                stateNode.add(itemNode)
            }
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
        if (it.isAddedPath) {
            val selectedNode = it.path.lastPathComponent as? DefaultMutableTreeNode
            selectedNode?.let { node ->
                val description = getAssignables().first { it.name == node.userObject.toString() }.description ?: "No description"
                onSelectNode(description)
            }
        }
    }
    TreeUtil.installActions(tree)
    return tree
}
