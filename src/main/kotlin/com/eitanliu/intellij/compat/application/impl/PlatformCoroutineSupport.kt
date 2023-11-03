package com.eitanliu.intellij.compat.application.impl

import com.eitanliu.intellij.compat.application.CoroutineSupport
import com.intellij.openapi.application.ModalityState
import kotlin.coroutines.CoroutineContext

internal open class PlatformCoroutineSupport : CoroutineSupport {

    companion object : PlatformCoroutineSupport() {
        override fun toString() = "EDTCoroutineSupport"
    }

    override fun asContextElement(modalityState: ModalityState): CoroutineContext {
        return ModalityStateElement(modalityState)
    }

    override fun edtDispatcher(): CoroutineContext {
        return EdtCoroutineDispatcher
    }
}
