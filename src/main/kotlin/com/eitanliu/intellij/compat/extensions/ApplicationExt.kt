package com.eitanliu.intellij.compat.extensions

import com.eitanliu.intellij.compat.application.EDTCompat
import com.intellij.openapi.application.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.concurrent.schedule
import kotlin.coroutines.CoroutineContext

private val edtContext: CoroutineContext = try {
    // Dispatchers.EDT
    val clazz = Class.forName("com.intellij.openapi.application.CoroutinesKt")
    val edtMethod = clazz.getMethod("getEDT", Dispatchers::class.java);
    edtMethod.invoke(null, Dispatchers) as CoroutineContext
} catch (e: Throwable) {
    Dispatchers.EDTCompat
}

// val ApplicationScope = MainScope()
val ApplicationScope = CoroutineScope(edtContext)

fun Application.invokeLater(delay: Long, runnable: Runnable) {
    Timer().schedule(delay) { invokeLater(runnable) }
}