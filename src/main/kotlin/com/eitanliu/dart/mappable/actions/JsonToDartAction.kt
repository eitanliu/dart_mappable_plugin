package com.eitanliu.dart.mappable.actions

import com.eitanliu.dart.mappable.extensions.filterInContent
import com.eitanliu.dart.mappable.extensions.invokeLater
import com.eitanliu.dart.mappable.ui.JsonInputDialog
import com.eitanliu.dart.mappable.utils.CommandUtils
import com.eitanliu.dart.mappable.utils.MessagesUtils
import com.intellij.CommonBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import io.flutter.pub.PubRoots
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

        val pubRoots = PubRoots.forModule(module).filterInContent(directory.virtualFile)
        if (MessagesUtils.isNotFlutterProject(pubRoots)) return
        val pubRoot = pubRoots.first()

        val directoryFactory = PsiDirectoryFactory.getInstance(directory.project)
        val packageName = directoryFactory.getQualifiedName(directory, true)

        val dialog = JsonInputDialog(project) Action@{ generator ->

            val fileName = generator.fileName

            val psiFile = directory.findFile(fileName)

            if (psiFile != null) {
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
                    doc.setText(generator.generatorClassesString())
                    documentManager.commitDocument(doc)
                    CodeStyleManager.getInstance(project).reformat(psi)
                }
            }
        }

        ApplicationManager.getApplication().invokeLater(600L) {
            FileDocumentManager.getInstance().saveDocument(doc)
            CommandUtils.executeFlutterPubCommand(
                project, pubRoot, "run build_runner build --delete-conflicting-outputs"
            ) {
                directory.virtualFile.refresh(false, false)
            }
        }

        // 获取项目根目录
        // project.stateStore.projectBasePath
        // project.guessProjectDir()
        // project.baseDir

        // 获取模块根目录
        // module.moduleFile?.parent
        // module.moduleNioFile.parent
        // module.stateStore.storageManager.expandMacro(StoragePathMacros.MODULE_FILE).parent
        // ProjectRootManager.getInstance(project).fileIndex.getContentRootForFile(file)


        // 获取项目全部文件
        // ProjectRootManager.getInstance(project).contentRoots
        // ProjectRootManager.getInstance(project).contentSourceRoots
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
        // 判断包含关系
        // ModuleRootManager.getInstance(module).fileIndex.isInContent( directory.virtualFile )

        // 获取 VirtualFile
        // LocalFileSystem.getInstance().findFileByNioFile()

        // PsiFile 获取 VirtualFile
        // pisFile.getVirtualFile()
        // VirtualFile 获取 PsiFile
        // PsiManager.getInstance(project).findFile()
    }
}
