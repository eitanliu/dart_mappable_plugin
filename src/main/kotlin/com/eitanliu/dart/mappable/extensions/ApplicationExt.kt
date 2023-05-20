package com.eitanliu.dart.mappable.extensions

import com.intellij.openapi.application.Application
import java.util.Timer
import kotlin.concurrent.schedule

fun Application.invokeLater(delay: Long, runnable: Runnable) {
    Timer().schedule(delay) { invokeLater(runnable) }
}