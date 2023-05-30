package com.eitanliu.dart.mappable.actions

import com.eitanliu.dart.mappable.utils.CommandUtils
import com.eitanliu.dart.mappable.utils.MessagesUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.*
import io.flutter.pub.PubRoot

class FlutterRunBuildRunnerAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        val project = event.getData(PlatformDataKeys.PROJECT) ?: return

        val dataContext = event.dataContext
        val module = LangDataKeys.MODULE.getData(dataContext) ?: return

        val navigatable = LangDataKeys.NAVIGATABLE.getData(dataContext)
            ?: LangDataKeys.PSI_FILE.getData(dataContext)
            ?: LangDataKeys.EDITOR.getData(dataContext)?.document?.let {
                PsiDocumentManager.getInstance(project).getPsiFile(it)
            }
        val moduleRoot = ModuleRootManager.getInstance(module)
        val directory = when (navigatable) {
            is PsiDirectory -> navigatable
            is PsiFile -> navigatable.containingDirectory
            else -> {
                moduleRoot.sourceRoots.asSequence().mapNotNull {
                    PsiManager.getInstance(project).findDirectory(it)
                }.firstOrNull() ?: moduleRoot.excludeRoots.asSequence().mapNotNull {
                    PsiManager.getInstance(project).findDirectory(it)
                }.firstOrNull()
            }
        } ?: return
        val psiFile = navigatable as? PsiFile

        // val pubRoots = PubRoots.forModule(module).filterInContent(directory.virtualFile)
        // if (MessagesUtils.isNotFlutterProject(pubRoots, false)) return
        // val pubRoot = pubRoots.first()
        val pubRoot = PubRoot.forFile(psiFile?.virtualFile ?: directory.virtualFile)
        if (MessagesUtils.isNotFlutterProject(pubRoot, false)) return

        ApplicationManager.getApplication().invokeLater {
            FileDocumentManager.getInstance().saveAllDocuments()
            CommandUtils.executeFlutterPubCommand(
                project, pubRoot, "run build_runner build --delete-conflicting-outputs"
            ) {
                pubRoot.root.refresh(true, true)
            }
        }
    }
}