@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.eitanliu.dart.mappable.extensions

import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0
import kotlin.reflect.full.createInstance

inline fun <V> KProperty0<V>.toGraphProperty(
    propertyGraph: PropertyGraph = createPropertyGraph()
): GraphProperty<V> = propertyGraph.lazyProperty(::get)

inline fun <V> KMutableProperty0<V>.toGraphProperty(
    propertyGraph: PropertyGraph = createPropertyGraph(),
    parentDisposable: Disposable? = null,
): GraphProperty<V> = propertyGraph.lazyProperty(::get).also { property ->
    property.afterChange(parentDisposable) {
        if (get() != it) set(it)
    }
}

fun createPropertyGraph() = try {
    PropertyGraph()
} catch (e: Throwable) {
    PropertyGraph::class.createInstance()
}

inline fun <T> PropertyGraph.propertyRef(ref: KProperty0<T>): GraphProperty<T> = ref.toGraphProperty(this)

inline fun <T> PropertyGraph.propertyRef(ref: KMutableProperty0<T>): GraphProperty<T> = ref.toGraphProperty(this)

inline fun <T> PropertyGraph.propertyOf(initial: T): GraphProperty<T> = property(initial)

inline fun <T> PropertyGraph.propertyOf(noinline initial: () -> T): GraphProperty<T> = lazyProperty(initial)

inline var <T> GraphProperty<T>.value
    get() = get()
    set(value) = set(value)

inline fun <T> GraphProperty<T>.copyBind(
    parentDisposable: Disposable,
    propertyGraph: PropertyGraph = createPropertyGraph(),
): GraphProperty<T> = propertyGraph.lazyProperty(::get).also { property ->
    property.afterChange(parentDisposable) {
        if (get() != it) set(it)
    }
}