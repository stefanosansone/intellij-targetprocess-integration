package com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.ui

import com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.getAssignables
import com.github.stefanosansone.intellijtargetprocessintegration.toolWindow.getStates
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.render.RenderingUtil
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.tree.TreeUtil
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

fun toolWindowListPanel(onSelectNode: () -> Unit): Tree {
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
                onSelectNode
            }
        }
    }
    TreeUtil.installActions(tree)
    return tree
}

fun toolWindowDetailPanel(): JPanel {
    return panel {
        row {
            label("Detail panel")
        }
    }
}



