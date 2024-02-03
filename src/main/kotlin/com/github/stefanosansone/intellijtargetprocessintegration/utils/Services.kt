package com.github.stefanosansone.intellijtargetprocessintegration.utils

import com.github.stefanosansone.intellijtargetprocessintegration.services.TargetProcessProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

val Project.TargetProcessProjectService
    get() = service<TargetProcessProjectService>()
