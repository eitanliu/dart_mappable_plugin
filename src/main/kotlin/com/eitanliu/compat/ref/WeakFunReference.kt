package com.eitanliu.compat.ref

import java.lang.ref.WeakReference

class WeakFun1Reference<T>(
    func: T.() -> Unit
) : Function1<T, Unit> {

    private val func = WeakReference(func)

    override fun invoke(p1: T) {
        func.get()?.invoke(p1)
    }

}