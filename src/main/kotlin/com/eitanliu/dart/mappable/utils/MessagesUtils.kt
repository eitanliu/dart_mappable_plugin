package com.eitanliu.dart.mappable.utils

import com.intellij.openapi.ui.Messages
import io.flutter.pub.PubRoot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

object MessagesUtils {

    fun isNotFlutterProject(pubRoots: List<PubRoot>, show: Boolean = true): Boolean {
        return isNotFlutterProject(show) { pubRoots.isNotEmpty() }
    }

    @OptIn(ExperimentalContracts::class)
    fun isNotFlutterProject(pubRoots: PubRoot?, show: Boolean = true): Boolean {
        contract {
            returns(false) implies (pubRoots != null)
        }
        return isNotFlutterProject(show) { pubRoots == null }
    }


    fun isNotFlutterProject(show: Boolean = true, predicate: () -> Boolean): Boolean {

        val res = predicate()
        if (res && show) {
            Messages.showInfoMessage("This project is not the flutter project", "Info")
        }
        return res
    }
}