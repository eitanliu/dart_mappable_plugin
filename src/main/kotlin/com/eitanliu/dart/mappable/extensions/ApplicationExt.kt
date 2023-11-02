package com.eitanliu.dart.mappable.extensions

import com.eitanliu.intellij.compat.application.EDTCompat
import com.intellij.openapi.application.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.concurrent.schedule

// val ApplicationScope = MainScope()
val ApplicationScope = CoroutineScope(Dispatchers.EDTCompat)

fun Application.invokeLater(delay: Long, runnable: Runnable) {
    Timer().schedule(delay) { invokeLater(runnable) }
}