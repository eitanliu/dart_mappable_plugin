package com.eitanliu.compat.application

import com.intellij.openapi.application.ModalityState
import org.jetbrains.annotations.ApiStatus
import kotlin.coroutines.CoroutineContext

@ApiStatus.Internal
interface CoroutineSupport {

    fun asContextElement(modalityState: ModalityState): CoroutineContext

    fun edtDispatcher(): CoroutineContext
}