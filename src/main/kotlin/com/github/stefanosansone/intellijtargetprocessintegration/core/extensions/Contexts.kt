package com.github.stefanosansone.intellijtargetprocessintegration.core.extensions

import com.github.stefanosansone.intellijtargetprocessintegration.api.model.Assignables
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

interface AssignablesContext : ProjectContext {
    val assignables: List<Assignables.Item>
}
interface ProjectContext {
    val project: Project
    val coroutineScope: CoroutineScope
}