package com.eitanliu.dart.mappable.observable

import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.observable.properties.PropertyGraph

@Suppress("NonExtendableApiUsage", "UnstableApiUsage")
class DisposableObservableMutableProperty<T>(
    private val property: ObservableMutableProperty<T>,
    private val parentDisposable: Disposable,
    private val propertyGraph: PropertyGraph?
) : ObservableMutableProperty<T>, GraphProperty<T> {

    override fun dependsOn(parent: ObservableProperty<*>, update: () -> T) {
        propertyGraph?.dependsOn(this, parent, update)
    }

    override fun afterPropagation(listener: () -> Unit) {
        propertyGraph?.afterPropagation(parentDisposable, listener)
    }

    override fun afterPropagation(parentDisposable: Disposable?, listener: () -> Unit) {
        propertyGraph?.afterPropagation(parentDisposable ?: this.parentDisposable, listener)
    }

    init {
        propertyGraph?.register(this)
    }

    override fun set(value: T) {
        property.set(value)
    }

    override fun afterChange(listener: (T) -> Unit) {
        property.afterChange(listener, parentDisposable)
    }

    override fun afterChange(listener: (T) -> Unit, parentDisposable: Disposable) {
        property.afterChange(listener, parentDisposable)
    }

    override fun get(): T {
        return property.get()
    }
}