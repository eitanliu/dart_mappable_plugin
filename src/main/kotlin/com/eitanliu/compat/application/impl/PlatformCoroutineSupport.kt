package com.eitanliu.compat.application.impl

import com.eitanliu.compat.application.CoroutineSupport
import com.intellij.openapi.application.ModalityState
import kotlin.coroutines.CoroutineContext

internal class PlatformCoroutineSupport : CoroutineSupport {

    override fun asContextElement(modalityState: ModalityState): CoroutineContext {
        return ModalityStateElement(modalityState)
    }

    override fun edtDispatcher(): CoroutineContext {
        return EdtCoroutineDispatcher
    }
}
