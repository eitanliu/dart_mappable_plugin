package com.eitanliu.dart.mappable.extensions

import com.intellij.util.containers.UnsafeWeakList
import java.lang.ref.WeakReference

fun <T> (T.() -> Unit).weakRef(): T.() -> Unit {
    return WeakFun1Reference(this)
}

class WeakFun1Reference<T>(
    func: T.() -> Unit
) : Function1<T, Unit> {

    private val func = WeakReference(func)

    override fun invoke(p1: T) {
        func.get()?.invoke(p1)
    }

}

class WeakRecFun1List<T>(receiver: T, capacity: Int) :
    UnsafeWeakList<T.() -> Unit>(capacity), Function0<Unit> {

    val weakRef = WeakReference(this)

    private val receiver: WeakReference<T>

    constructor(receiver: T) : this(receiver, 0)

    init {
        this.receiver = WeakReference(receiver)
    }

    override fun invoke() = forEach {
        receiver.get()?.apply { it?.invoke(this) }
    }

}