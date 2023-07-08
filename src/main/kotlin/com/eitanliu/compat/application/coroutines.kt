package com.eitanliu.compat.application

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asContextElement
import kotlin.coroutines.CoroutineContext

/**
 * The code [without][ModalityState.any] context modality state must only perform pure UI operations,
 * it must not access any PSI, VFS, project model, or indexes.
 */
fun ModalityState.asContextElement(): CoroutineContext = coroutineSupport().asContextElement(this)

/**
 * @return UI dispatcher which dispatches within the [context modality state][asContextElement].
 */
val Dispatchers.EDTCompat: CoroutineContext get() = coroutineSupport().edtDispatcher()

private fun coroutineSupport() = ApplicationManager.getApplication().getService(CoroutineSupport::class.java)