package com.eitanliu.dart.mappable.services

import com.eitanliu.dart.mappable.DartMappableBundle
import com.intellij.openapi.project.Project

class ProjectService(project: Project) {

    init {
        println(DartMappableBundle.message("project.service", project.name))
    }
}
