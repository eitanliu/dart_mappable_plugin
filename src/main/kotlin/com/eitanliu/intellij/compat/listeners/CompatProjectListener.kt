package com.eitanliu.intellij.compat.listeners

import com.intellij.openapi.project.Project

interface CompatProjectListener {

    /**
     * mark removal API version 223.8617.56 (2022.3.2).
     */
    fun projectOpened(project: Project) {}
}