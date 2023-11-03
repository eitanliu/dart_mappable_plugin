package com.eitanliu.dart.mappable.actions

import com.eitanliu.intellij.compat.extensions.ApplicationScope
import com.eitanliu.intellij.compat.extensions.value
import com.eitanliu.dart.mappable.ui.JsonInputDialog
import com.eitanliu.dart.mappable.utils.ApplicationUtils
import com.eitanliu.dart.mappable.utils.CommandUtils
import com.eitanliu.dart.mappable.utils.DependenciesUtils
import com.eitanliu.dart.mappable.utils.MessagesUtils
import com.intellij.CommonBundle
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import io.flutter.pub.PubRoot
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import java.io.IOException


class JsonToDartAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        val project = event.getData(PlatformDataKeys.PROJECT) ?: return
        val projectContext = event.getData(PlatformDataKeys.PROJECT_CONTEXT)

        val dataContext = event.dataContext
        val module = LangDataKeys.MODULE.getData(dataContext) ?: return
        val moduleContext = LangDataKeys.MODULE_CONTEXT.getData(dataContext)
        val moduleArray = LangDataKeys.MODULE_CONTEXT_ARRAY.getData(dataContext)

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
                }.firstOrNull()
            }
        } ?: return
        val psiFile = navigatable as? PsiFile

        // val pubRoots = PubRoots.forModule(module).filterInContent(directory.virtualFile)
        // if (MessagesUtils.isNotFlutterProject(pubRoots)) return
        // val pubRoot = pubRoots.first()
        val pubRoot = PubRoot.forFile(psiFile?.virtualFile ?: directory.virtualFile)
        if (MessagesUtils.isNotFlutterProject(pubRoot)) return

        val directoryFactory = PsiDirectoryFactory.getInstance(directory.project)
        val packageName = directoryFactory.getQualifiedName(directory, true)

        val dialog = JsonInputDialog(project) Action@{ generator ->

            val fileName = generator.fileName

            val psi = directory.findFile(fileName)

            if (psi != null) {
                val override = Messages.showOkCancelDialog(
                    "Do you want to overwrite the $fileName file?", "File Already Exist",
                    CommonBundle.message("button.overwrite"), CommonBundle.getCancelButtonText(),
                    null,
                )
                return@Action override == Messages.OK
            }
            true
        }.showDialog()

        val generator = dialog.generator ?: return

        val documentManager = PsiDocumentManager.getInstance(project)
        val doc = WriteCommandAction.runWriteCommandAction<Document, IOException>(project) {
            val file = directory.virtualFile.findOrCreateChildData(this, generator.fileName)
            PsiManager.getInstance(project).findFile(file)?.let { psi ->
                documentManager.getDocument(psi)?.also { doc ->
                    doc.setText(generator.buildString())
                    documentManager.commitDocument(doc)
                    // CodeStyleManager.getInstance(project).reformat(psi)
                    ReformatCodeProcessor(psi, false).run()
                }
            }
        }

        // ApplicationManager.getApplication().invokeLater(600L) {
        ApplicationScope.launch {
            delay(600L)

            val moduleEntity = DependenciesUtils.loadModule(project, module, pubRoot)
            val settings = ApplicationUtils.getSettings()
            val deps = DependenciesUtils.checkDependencies(settings, moduleEntity.pubspec)
            // println("deps ${deps.joinToString()}")
            for (dep in deps) {
                CommandUtils.executeFlutterPubCommand(project, pubRoot, dep).await()
            }

            FileDocumentManager.getInstance().saveDocument(doc)
            if (!settings.graph.autoBuildRunner.value) return@launch
            CommandUtils.executeFlutterPubCommand(
                project, pubRoot, "run build_runner build --delete-conflicting-outputs"
            ) {
                directory.virtualFile.refresh(false, false)
            }
        }
    }
}
