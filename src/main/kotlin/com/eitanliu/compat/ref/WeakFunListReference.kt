package com.eitanliu.compat.ref

import com.intellij.util.containers.WeakList
import java.lang.ref.WeakReference

class WeakFun1List<T>(
    receiver: T, list: WeakList<T.() -> Unit> = WeakList()
) : Function0<Unit>, MutableCollection<T.() -> Unit> by list {

    val receiver: WeakReference<T>

    val weakRef = WeakReference(this)

    val weakFun get() = WeakFunction(weakRef)

    init {
        this.receiver = WeakReference(receiver)
    }

    override val size: Int
        get() = throw UnsupportedOperationException("${this::class.simpleName} are not supported")

    override fun invoke() = forEach {
        receiver.get()?.apply { it.invoke(this) }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class WeakFunction<T>(val weakRef: WeakReference<WeakFun1List<T>>) : Function0<Unit> {
        override fun invoke() {
            weakRef.get()?.invoke()
        }

    }

}