package com.eitanliu.dart.mappable.utils

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import io.flutter.pub.PubRoot

@Suppress("MemberVisibilityCanBePrivate")
object FileUtils {

    fun pubRoot(module: Module) = pubRoot(pubSpecRootDirs(module))

    fun pubRoot(project: Project) = pubRoot(pubSpecRootDirs(project))

    fun pubRoot(scope: GlobalSearchScope) = pubRoot(pubSpecRootDirs(scope))

    fun pubRoot(dirs: Sequence<VirtualFile>) = dirs
        .map { PubRoot.forDirectory(it) }

    fun pubSpecRootDirs(module: Module) = pubSpecRootDirs(searchScope(module))

    fun pubSpecRootDirs(project: Project) = pubSpecRootDirs(searchScope(project))

    fun pubSpecRootDirs(scope: GlobalSearchScope) = pubSpecRootDirs(pubSpecYamlFiles(scope))

    fun pubSpecRootDirs(yamlFiles: Sequence<VirtualFile>) = yamlFiles.map { it.parent }
        .sortedByDescending { it.path }

    fun pubSpecYamlFiles(module: Module) = pubSpecYamlFiles(searchScope(module))

    fun pubSpecYamlFiles(project: Project) = pubSpecYamlFiles(searchScope(project))

    fun pubSpecYamlFiles(scope: GlobalSearchScope) = FilenameIndex.getVirtualFilesByName("pubspec.yaml", scope)
        .asSequence()
        .filter { !it.isDirectory }
        .sortedByDescending { it.path.length }

    fun searchScope(module: Module): GlobalSearchScope = GlobalSearchScope.moduleScope(module)

    fun searchScope(project: Project): GlobalSearchScope = GlobalSearchScope.projectScope(project)

}