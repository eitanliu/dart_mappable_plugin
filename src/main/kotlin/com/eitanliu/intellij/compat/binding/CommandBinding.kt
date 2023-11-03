package com.eitanliu.intellij.compat.binding

import com.eitanliu.intellij.compat.utils.SimpleKeyListener
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.ui.layout.ComponentPredicate
import java.awt.Component
import java.awt.event.KeyEvent
import java.util.concurrent.atomic.AtomicBoolean

fun <T> GraphProperty<T>.selected(expected: T) = object : ComponentPredicate() {
    override fun invoke(): Boolean = get() == expected

    override fun addListener(listener: (Boolean) -> Unit) {
        afterChange {
            listener(it == expected)
        }
    }
}

fun AtomicBoolean.lockOrSkip(action: () -> Unit) {
    if (!compareAndSet(false, true)) return
    try {
        action()
    } finally {
        set(false)
    }
}

fun Component.bindTabTransferFocus() {
    addKeyListener(SimpleKeyListener(onKeyPressed = { e ->
        if (e.keyCode == KeyEvent.VK_TAB) {
            e.consume()
            if (e.isShiftDown) {
                transferFocusBackward()
            } else {
                transferFocus()
            }
        }
    }))
}