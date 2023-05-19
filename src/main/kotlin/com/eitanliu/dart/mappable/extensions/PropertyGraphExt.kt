@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.eitanliu.dart.mappable.extensions

import com.eitanliu.dart.mappable.observable.DisposableObservableMutableProperty
import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.GraphPropertyImpl
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

@Suppress("UnstableApiUsage")
inline fun <V> KProperty0<V>.toGraphProperty(
    propertyGraph: PropertyGraph = PropertyGraph()
): GraphProperty<V> = GraphPropertyImpl(propertyGraph, ::get)

@Suppress("UnstableApiUsage")
inline fun <V> KMutableProperty0<V>.toGraphProperty(
    propertyGraph: PropertyGraph = PropertyGraph()
): GraphProperty<V> = GraphPropertyImpl(propertyGraph, ::get).also { property ->
    property.afterChange {
        if (get() != it) set(it)
    }
}

inline fun <T> PropertyGraph.propertyRef(ref: KProperty0<T>): GraphProperty<T> = ref.toGraphProperty(this)

inline fun <T> PropertyGraph.propertyRef(ref: KMutableProperty0<T>): GraphProperty<T> = ref.toGraphProperty(this)

@Suppress("UnstableApiUsage")
inline fun <T> PropertyGraph.propertyOf(initial: T): GraphProperty<T> = GraphPropertyImpl(this) { initial }

@Suppress("UnstableApiUsage")
inline fun <T> PropertyGraph.propertyOf(noinline initial: () -> T): GraphProperty<T> =
    GraphPropertyImpl(this, initial)

inline var <T> GraphProperty<T>.value
    get() = get()
    set(value) = set(value)

inline fun <T> ObservableMutableProperty<T>.copyBind(
    parentDisposable: Disposable,
    propertyGraph: PropertyGraph? = null
): ObservableMutableProperty<T> =
    DisposableObservableMutableProperty(this, parentDisposable, propertyGraph)