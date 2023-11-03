package com.eitanliu.intellij.compat.utils

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class SimpleKeyListener(
    private val onKeyTyped: (e: KeyEvent) -> Unit = {},
    private val onKeyPressed: (e: KeyEvent) -> Unit = {},
    private val onKeyReleased: (e: KeyEvent) -> Unit = {},
) : KeyListener {
    override fun keyTyped(e: KeyEvent) = onKeyTyped(e)

    override fun keyPressed(e: KeyEvent)= onKeyPressed(e)

    override fun keyReleased(e: KeyEvent)= onKeyReleased(e)
}