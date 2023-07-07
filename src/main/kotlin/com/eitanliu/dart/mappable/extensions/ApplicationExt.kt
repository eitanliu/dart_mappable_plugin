package com.eitanliu.dart.mappable.extensions

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.EDT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.concurrent.schedule

// val ApplicationScope = MainScope()
val ApplicationScope = CoroutineScope(Dispatchers.EDT)

fun Application.invokeLater(delay: Long, runnable: Runnable) {
    Timer().schedule(delay) { invokeLater(runnable) }
}