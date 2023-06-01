package com.eitanliu.dart.mappable.extensions

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Disposer

fun DialogPanel.registerDisposer(parent: Disposable, child: Disposable? = null) = run {
    val disposable = child ?: Disposer.newDisposable()
    registerValidators(disposable)
    Disposer.register(parent, disposable)
    disposable
}