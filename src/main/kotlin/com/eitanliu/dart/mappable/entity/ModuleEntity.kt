package com.eitanliu.dart.mappable.entity

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import io.flutter.pub.PubRoot

data class ModuleEntity(
    val project: Project,
    val module: Module,
    val pubRoot: PubRoot,
    val pubspec: PubspecEntity,
)

data class PubspecEntity(
    val yaml: Map<String, Any?>,
    val lock: Map<String, Any?>?,
)

data class DependenceEntity(
    val name: String,
    val scope: String = SCOPE_EMPTY,
) {
    companion object {
        const val SCOPE_EMPTY = ""
        const val SCOPE_DEV = "dev"

        val JSON_REFLECTABLE_VALUES = listOf(
            DependenceEntity("json_reflectable"),
            DependenceEntity("build_runner", SCOPE_DEV),
        )

        val JSON_SERIALIZABLE_VALUES = listOf(
            DependenceEntity("json_annotation"),
            DependenceEntity("json_serializable", SCOPE_DEV),
            DependenceEntity("build_runner", SCOPE_DEV),
        )

        val DART_MAPPABLE_VALUES = listOf(
            DependenceEntity("dart_mappable"),
            DependenceEntity("dart_mappable_builder", SCOPE_DEV),
            DependenceEntity("build_runner", SCOPE_DEV),
        )

        val FREEZED_VALUES = listOf(
            DependenceEntity("freezed_annotation"),
            DependenceEntity("freezed", SCOPE_DEV),
            DependenceEntity("build_runner", SCOPE_DEV),
        )
    }
}