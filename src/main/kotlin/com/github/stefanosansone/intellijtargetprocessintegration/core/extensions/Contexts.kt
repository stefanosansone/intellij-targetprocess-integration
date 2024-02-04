package com.github.stefanosansone.intellijtargetprocessintegration.core.extensions

import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

interface ProjectContext {
    val project: Project
    val coroutineScope: CoroutineScope
}