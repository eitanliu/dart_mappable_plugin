package com.eitanliu.dart.mappable.extensions

import com.intellij.util.containers.WeakList
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

class WeakRecFun1List<T>(
    receiver: T, list: WeakList<T.() -> Unit> = WeakList()
) : Function0<Unit>, MutableCollection<T.() -> Unit> by list {

    val weakRef = WeakReference(this)

    private val receiver: WeakReference<T>

    val listener = Listener(weakRef)

    init {
        this.receiver = WeakReference(receiver)
    }

    override fun invoke() = forEach {
        receiver.get()?.apply { it.invoke(this) }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class Listener<T>(val weakRef: WeakReference<WeakRecFun1List<T>>) : Function0<Unit> {
        override fun invoke() {
            weakRef.get()?.invoke()
        }

    }

}