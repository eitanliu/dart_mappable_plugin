package com.eitanliu.dart.mappable.observable

import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.util.containers.WeakList
import java.lang.ref.WeakReference

class DisposableObservableMutableProperty<T>(
    property: ObservableMutableProperty<T>,
    parentDisposable: Disposable,
) : ObservableMutableProperty<T> {

    private val property = WeakReference(property)
    private val disposable = WeakReference(parentDisposable)
    private val listeners = WeakList<(T) -> Unit>()
    private var cache = property.get()

    override fun set(value: T) {
        cache = value
        property.get()?.set(value) ?: listeners.forEach {
            it?.invoke(value)
        }
    }

    override fun afterChange(listener: (T) -> Unit) {
        listeners.add(listener)
        disposable.get()?.also {
            property.get()?.afterChange(listener, it)
        }
    }

    override fun afterChange(listener: (T) -> Unit, parentDisposable: Disposable) {
        listeners.add(listener)
        property.get()?.afterChange(listener, parentDisposable)
    }

    override fun get(): T {
        return property.get()?.get() ?: cache
    }
}