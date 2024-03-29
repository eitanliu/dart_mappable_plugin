package com.eitanliu.intellij.compat.extensions

import com.eitanliu.intellij.compat.ref.WeakFun1Reference

fun <T> (T.() -> Unit).weakRef(): T.() -> Unit {
    return WeakFun1Reference(this)
}

