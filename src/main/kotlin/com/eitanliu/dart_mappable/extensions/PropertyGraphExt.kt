@file:Suppress("unused")

package com.eitanliu.dart_mappable.extensions

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.GraphPropertyImpl
import com.intellij.openapi.observable.properties.PropertyGraph
import kotlin.reflect.KMutableProperty0

@Suppress("NOTHING_TO_INLINE", "UnstableApiUsage")
inline fun <V> KMutableProperty0<V>.toGraphProperty(
    propertyGraph: PropertyGraph = PropertyGraph()
) = GraphPropertyImpl(propertyGraph, ::get).also { property ->
    property.afterChange {
        if (get() != it) set(it)
    }
}

@Suppress("NOTHING_TO_INLINE", "EXTENSION_SHADOWED_BY_MEMBER")
inline fun <T> PropertyGraph.graphProperty(ref: KMutableProperty0<T>): GraphProperty<T> = ref.toGraphProperty(this)