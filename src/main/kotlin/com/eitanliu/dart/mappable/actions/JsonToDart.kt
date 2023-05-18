package com.eitanliu.dart.mappable.actions

import com.eitanliu.dart.mappable.extensions.camelCaseToUnderscore
import com.eitanliu.dart.mappable.generator.DartGenerator
import com.eitanliu.dart.mappable.settings.Settings
import com.eitanliu.dart.mappable.ui.JsonInputDialog
import com.intellij.CommonBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.components.stateStore
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.project.stateStore
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import io.flutter.pub.PubRoots

class JsonToDart : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        val project = event.getData(PlatformDataKeys.PROJECT) ?: return
        val projectContext = event.getData(PlatformDataKeys.PROJECT_CONTEXT)
        println("project $project")

        val dataContext = event.dataContext
        val module = LangDataKeys.MODULE.getData(dataContext) ?: return
        val moduleContext = LangDataKeys.MODULE_CONTEXT.getData(dataContext)
        val moduleArray = LangDataKeys.MODULE_CONTEXT_ARRAY.getData(dataContext)

        val navigatable = LangDataKeys.NAVIGATABLE.getData(dataContext)
        val pubRoots = PubRoots.forModule(module)
        val moduleRoot = ModuleRootManager.getInstance(module)
        val directory = when (navigatable) {
            is PsiDirectory -> navigatable
            is PsiFile -> navigatable.containingDirectory
            else -> {
                println("${moduleRoot.sourceRoots.size}")
                moduleRoot.sourceRoots
                    .asSequence()
                    .mapNotNull {
                        println("${it.canonicalPath}")
                        PsiManager.getInstance(project).findDirectory(it)
                    }.firstOrNull()
            }
        } ?: return
        val directoryFactory = PsiDirectoryFactory.getInstance(directory.project)
        val packageName = directoryFactory.getQualifiedName(directory, true)
        val psiFileFactory = PsiFileFactory.getInstance(project)

        val (className, json) = JsonInputDialog(project) { className, json ->
            val name = className.camelCaseToUnderscore()
            val fileName = "$name.dart"

            val psiFile = directory.findFile(fileName)

            if (psiFile != null) {
                val override = Messages.showOkCancelDialog(
                    "Do you want to overwrite the current file?", "File Already Exist",
                    CommonBundle.message("button.overwrite"), CommonBundle.getCancelButtonText(),
                    null,
                )
                return@JsonInputDialog override == Messages.OK
            }
            true
        }.showDialog()

        val settings = ApplicationManager.getApplication().getService(Settings::class.java).state
        val generator = DartGenerator(settings, className, json)

        // 获取项目根目录
        project.stateStore.projectBasePath
        project.guessProjectDir()
        project.baseDir

        // 获取模块根目录
        module.moduleFile?.parent
        module.moduleNioFile.parent
        module.stateStore.storageManager.expandMacro(StoragePathMacros.MODULE_FILE).parent
        // ProjectRootManager.getInstance(project).fileIndex.getContentRootForFile(file)


        // 获取项目全部文件
        ProjectRootManager.getInstance(project).contentRoots
        ProjectRootManager.getInstance(project).contentSourceRoots
        // 获取模块全部文件
        // ModuleRootManager.getInstance(module).contentRoots
        // 获取模块全部文件(不包含根目录)
        // ModuleRootManager.getInstance(module).excludeRoots
        // 获取模块源码文件
        // ModuleRootManager.getInstance(module).sourceRoots
        // ModuleRootManager.getInstance(module).externalSource
        // ModuleRootManager.getInstance(module).getSourceRoots(rootTypes)
        // 遍历目录
        // ModuleRootManager.getInstance(module).fileIndex.iterateContent {  }

        // 获取 VirtualFile
        // LocalFileSystem.getInstance().findFileByNioFile()

        // PsiFile 获取 VirtualFile
        // pisFile.getVirtualFile()
        // VirtualFile 获取 PsiFile
        // PsiManager.getInstance(project).findFile()
    }
}
