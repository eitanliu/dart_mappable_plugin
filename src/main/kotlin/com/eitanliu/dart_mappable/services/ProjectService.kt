package com.eitanliu.dart_mappable.services

import com.eitanliu.dart_mappable.DartMappableBundle
import com.intellij.openapi.project.Project

class ProjectService(project: Project) {

    init {
        println(DartMappableBundle.message("projectService", project.name))
    }
}
