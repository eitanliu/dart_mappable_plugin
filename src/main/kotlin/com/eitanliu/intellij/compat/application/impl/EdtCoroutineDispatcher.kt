package com.eitanliu.intellij.compat.application.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.util.ui.EDT
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.job
import kotlin.coroutines.CoroutineContext


/**
 * Default UI dispatcher, it dispatches within the [context modality state][com.intellij.openapi.application.asContextElement].
 *
 * Cancelled coroutines are dispatched [without modality state][ModalityState.any] to be able to finish fast.
 * Such coroutines are not expected to access the model (PSI/VFS/etc).
 *
 * This dispatcher is installed as [main][kotlinx.coroutines.Dispatchers.Main].
 */
internal sealed class EdtCoroutineDispatcher : MainCoroutineDispatcher() {

    override val immediate: MainCoroutineDispatcher get() = Immediate

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        val state = context.contextModality()
        val runnable = if (state === ModalityState.defaultModalityState()) {
            block
        } else {
            DispatchedRunnable(state, context.job, block)
        }
        ApplicationManager.getApplication().invokeLater(runnable, state)
    }

    companion object : EdtCoroutineDispatcher() {

        override fun toString() = "EDTCompat"
    }

    object Immediate : EdtCoroutineDispatcher() {

        override fun isDispatchNeeded(context: CoroutineContext): Boolean {
            if (!EDT.isCurrentThreadEdt()) {
                return true
            }
            // The current coroutine is executed with the correct modality state
            // (the execution would be postponed otherwise)
            // => there is no need to check modality state here.
            return false
        }

        override fun toString() = "EDTCompat.immediate"
    }
}
