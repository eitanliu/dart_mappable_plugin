package com.eitanliu.dart.mappable.extensions

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer

fun Disposable.whenDisposed(listener: () -> Unit) {
    Disposer.register(this, listener)
}