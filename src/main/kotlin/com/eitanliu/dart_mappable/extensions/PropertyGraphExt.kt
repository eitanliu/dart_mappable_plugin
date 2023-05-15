@file:Suppress("unused")

package com.eitanliu.dart_mappable.extensions

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.GraphPropertyImpl
import com.intellij.openapi.observable.properties.PropertyGraph
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

@Suppress("NOTHING_TO_INLINE", "UnstableApiUsage")
inline fun <V> KProperty0<V>.toGraphProperty(
    propertyGraph: PropertyGraph = PropertyGraph()
): GraphProperty<V> = GraphPropertyImpl(propertyGraph, ::get)

@Suppress("NOTHING_TO_INLINE", "UnstableApiUsage")
inline fun <V> KMutableProperty0<V>.toGraphProperty(
    propertyGraph: PropertyGraph = PropertyGraph()
): GraphProperty<V> = GraphPropertyImpl(propertyGraph, ::get).also { property ->
    property.afterChange {
        if (get() != it) set(it)
    }
}
@Suppress("NOTHING_TO_INLINE")
inline fun <T> PropertyGraph.propertyOf(ref: KProperty0<T>): GraphProperty<T> = ref.toGraphProperty(this)

@Suppress("NOTHING_TO_INLINE")
inline fun <T> PropertyGraph.propertyOf(ref: KMutableProperty0<T>): GraphProperty<T> = ref.toGraphProperty(this)

@Suppress("NOTHING_TO_INLINE", "UnstableApiUsage")
inline fun <T> PropertyGraph.propertyValue(initial: T): GraphProperty<T> = GraphPropertyImpl(this) { initial }

@Suppress("NOTHING_TO_INLINE", "UnstableApiUsage")
inline fun <T> PropertyGraph.propertyValue(noinline initial: () -> T): GraphProperty<T> =
    GraphPropertyImpl(this, initial)

inline var <T> GraphProperty<T>.value
    get() = get()
    set(value) = set(value)