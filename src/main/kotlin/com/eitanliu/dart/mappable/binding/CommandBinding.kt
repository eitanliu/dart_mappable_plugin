package com.eitanliu.dart.mappable.binding

import com.eitanliu.dart.mappable.utils.SimpleKeyListener
import java.awt.Component
import java.awt.event.KeyEvent
import java.util.concurrent.atomic.AtomicBoolean

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