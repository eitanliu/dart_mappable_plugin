package com.eitanliu.dart_mappable.actions

import com.eitanliu.dart_mappable.ui.JsonInputDialog
import com.eitanliu.dart_mappable.utils.Log
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
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
        val directory = when (navigatable) {
           is PsiDirectory -> navigatable
           is PsiFile -> navigatable.containingDirectory
            else -> {
                val root = ModuleRootManager.getInstance(module)
                println("${root.sourceRoots.size}")
                root.sourceRoots
                        .asSequence()
                        .mapNotNull {
                            println("${it.canonicalPath}")
                            PsiManager.getInstance(project).findDirectory(it)
                        }.firstOrNull()
            }
        } //?: return
//        val directoryFactory = PsiDirectoryFactory.getInstance(directory.project)
//        val packageName = directoryFactory.getQualifiedName(directory, true)
//         val psiFileFactory = PsiFileFactory.getInstance(project)
        JsonInputDialog(project) { className, json ->
            false
        }.show()

    }
}
