package com.eitanliu.dart.mappable.extensions

import com.eitanliu.compat.ref.WeakFun1Reference

fun <T> (T.() -> Unit).weakRef(): T.() -> Unit {
    return WeakFun1Reference(this)
}

