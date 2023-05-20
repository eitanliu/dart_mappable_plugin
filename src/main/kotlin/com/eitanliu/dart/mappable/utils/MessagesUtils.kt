package com.eitanliu.dart.mappable.utils

import com.intellij.openapi.ui.Messages
import io.flutter.pub.PubRoot

object MessagesUtils {
    fun isNotFlutterProject(pubRoots: List<PubRoot>): Boolean {
        if (pubRoots.isEmpty()) {
            Messages.showInfoMessage("This project is not the flutter project", "Info")
        }
        return pubRoots.isEmpty()
    }
}