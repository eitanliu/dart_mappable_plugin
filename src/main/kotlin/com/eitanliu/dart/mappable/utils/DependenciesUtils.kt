package com.eitanliu.dart.mappable.utils

import com.eitanliu.dart.mappable.entity.DependenceEntity
import com.eitanliu.dart.mappable.entity.ModuleEntity
import com.eitanliu.dart.mappable.entity.PubspecEntity
import com.eitanliu.dart.mappable.extensions.loadYaml
import com.eitanliu.dart.mappable.extensions.value
import com.eitanliu.dart.mappable.settings.Implements
import com.eitanliu.dart.mappable.settings.Settings
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import io.flutter.pub.PubRoot

object DependenciesUtils {

    fun checkDependencies(settings: Settings, pubspec: PubspecEntity) = sequence {
        val enableJsonReflectable = settings.graph.enableJsonReflectable.value
        val implement = settings.graph.implement.value
        if (enableJsonReflectable)
            yieldAll(DependenceEntity.JSON_REFLECTABLE_VALUES)
        when (implement) {
            Implements.DART_MAPPABLE -> yieldAll(DependenceEntity.DART_MAPPABLE_VALUES)
            Implements.JSON_SERIALIZABLE -> yieldAll(DependenceEntity.JSON_SERIALIZABLE_VALUES)
        }
    }.distinct().filterNot filter@{
        val dependencies = pubspec.yaml["dependencies"]
        val devDependencies = pubspec.yaml["dev_dependencies"]
        val packages = pubspec.lock?.get("packages")
        if (it.scope == DependenceEntity.SCOPE_DEV) {
            if (devDependencies is Map<*, *>) {
                return@filter devDependencies.keys.contains(it.name)
            } else if (dependencies is Map<*, *>) {
                return@filter dependencies.keys.contains(it.name)
            }
        } else {
            if (dependencies is Map<*, *>) {
                return@filter dependencies.keys.contains(it.name)
            } else if (packages is Map<*, *>) {
                return@filter packages.keys.contains(it.name)
            }
        }
        false
    }.map {
        buildString {
            append("add ${it.name}")
            if (it.scope != DependenceEntity.SCOPE_EMPTY) append(" --${it.scope}")
        }
    }

    fun loadModule(project: Project, module: Module, pubRoot: PubRoot) = run {
        val pubspec = pubRoot.pubspec
        val pubspecLock = pubRoot.root.findChild("pubspec.lock")
        ModuleEntity(
            project, module, pubRoot,
            PubspecEntity(pubspec.loadYaml(), pubspecLock?.loadYaml()),
        )
    }
}