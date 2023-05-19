package com.eitanliu.dart.mappable.utils

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.SystemInfo
import io.flutter.pub.PubRoot
import io.flutter.sdk.FlutterSdk

object CommandUtils {
    fun executeCommand(project: Project, command: String) {
        val executable = if (SystemInfo.isWindows) "cmd.exe" else "bash"

        // 指定工作目录
        val workingDirectory: String = project.basePath ?: return
        val commandLine = GeneralCommandLine()
        commandLine.exePath = executable
        commandLine.setWorkDirectory(workingDirectory)
        commandLine.withParameters(if (SystemInfo.isWindows) "/c" else "-c", command)

        // FlutterConsoles.displayMessage(project, null , workingDirectory, true)
        // FlutterConsoles.displayMessage(project, null , "$command\n", )
        try {
            // val handler: ColoredProcessHandler = MostlySilentColoredProcessHandler(commandLine)
            //
            // FlutterConsoles.displayProcessLater(
            //     handler, project, null as Module?
            // ) { handler.startNotify() }

            val processHandler = OSProcessHandler(commandLine)
            processHandler.addProcessListener(object : ProcessAdapter() {
                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    // 获取命令行输出文本
                    val text = event.text

                    // 根据输出类型进行处理
                    if (outputType == ProcessOutputTypes.STDOUT) {
                        // 标准输出
                        // FlutterConsoles.displayMessage(project, null , text)
                        System.out.println(text)
                    } else if (outputType == ProcessOutputTypes.STDERR) {
                        // 错误输出
                        System.err.println(text)
                    }
                }
            })
            processHandler.startNotify()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }

    fun executeFlutterPubCommand(project: Project, root: PubRoot, args: String) {
        val sdk = FlutterSdk.getFlutterSdk(project) ?: return
        val module = root.getModule(project) ?: return
        // FileDocumentManager.getInstance().saveAllDocuments()
        val command = sdk.flutterPub(root, *args.split(' ').toTypedArray())
        // command.startInModuleConsole(module, root::refresh, null)
        command.startInConsole(project)
    }
}