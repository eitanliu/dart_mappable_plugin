package com.eitanliu.dart.mappable.utils

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.SystemInfo
import io.flutter.console.FlutterConsoles
import io.flutter.pub.PubRoot
import io.flutter.sdk.FlutterSdk
import io.flutter.utils.MostlySilentColoredProcessHandler

object CommandUtils {
    fun executeCommand(project: Project, command: String) {
        val executable = if (SystemInfo.isWindows) "cmd.exe" else "bash"

        val workingDirectory: String = project.basePath ?: return
        val commandLine = GeneralCommandLine()
        commandLine.exePath = executable
        commandLine.setWorkDirectory(workingDirectory)
        commandLine.withParameters(if (SystemInfo.isWindows) "/c" else "-c", command)

        try {
            val handler: ColoredProcessHandler = MostlySilentColoredProcessHandler(commandLine)

            FlutterConsoles.displayProcessLater(
                handler, project, null
            ) { handler.startNotify() }

            // FlutterConsoles.displayMessage(project, null , "$workingDirectory\n", true)
            // FlutterConsoles.displayMessage(project, null , "$command\n", )
            // val processHandler = OSProcessHandler(commandLine)
            // processHandler.addProcessListener(object : ProcessAdapter() {
            //     override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
            //         val text = event.text
            //         when (outputType) {
            //             ProcessOutputTypes.STDOUT, ProcessOutputTypes.STDERR ->
            //                 FlutterConsoles.displayMessage(project, null, text)
            //         }
            //     }
            // })
            // processHandler.startNotify()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }

    fun executeFlutterPubCommand(
        project: Project, root: PubRoot, args: String,
        processListener: ProcessListener? = null, onDone: Runnable? = null
    ) {
        val sdk = FlutterSdk.getFlutterSdk(project) ?: return
        val module = root.getModule(project) ?: return
        val command = sdk.flutterPub(root, *args.split(' ').toTypedArray())
        command.startInModuleConsole(module, onDone, processListener)
        // command.startInConsole(project)
    }
}