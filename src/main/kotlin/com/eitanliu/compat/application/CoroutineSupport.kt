package com.eitanliu.compat.application

import com.intellij.openapi.application.ModalityState
import kotlin.coroutines.CoroutineContext

interface CoroutineSupport {

    fun asContextElement(modalityState: ModalityState): CoroutineContext

    fun edtDispatcher(): CoroutineContext
}