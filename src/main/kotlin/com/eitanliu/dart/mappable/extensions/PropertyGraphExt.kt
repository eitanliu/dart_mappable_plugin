@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.eitanliu.dart.mappable.extensions

import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.GraphPropertyImpl.Companion.graphProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import java.lang.reflect.Constructor
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

inline fun <V> KProperty0<V>.toGraphProperty(
    propertyGraph: PropertyGraph = createPropertyGraph()
): GraphProperty<V> = propertyGraph.propertyOf(::get)

inline fun <V> KMutableProperty0<V>.toGraphProperty(
    propertyGraph: PropertyGraph = createPropertyGraph(),
    parentDisposable: Disposable? = null,
): GraphProperty<V> = propertyGraph.propertyOf(::get).also { property ->
    if (parentDisposable != null) {
        property.afterChange({
            if (get() != it) set(it)
        }, parentDisposable)
    } else {
        property.afterChange {
            if (get() != it) set(it)
        }
    }
}

fun createPropertyGraph() = try {
    PropertyGraph()
} catch (e: Throwable) {
    PropertyGraph::class.java.createInstance({ sequence ->
        sequence.sortedByDescending { it.parameterCount }
            .first { it.parameterCount <= 2 }
    }) {
        when (it.parameterCount) {
            2 -> arrayOf(null, true)
            else -> null
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Class<T>.createInstance(
    predicate: ((sequence: Sequence<Constructor<*>>) -> Constructor<*>?)? = null,
    params: ((Constructor<*>) -> Array<*>?)? = null,
): T = run {
    val sequence = constructors.asSequence()
    val constructor = predicate?.invoke(sequence) ?: sequence.first()
    val args = params?.invoke(constructor) ?: arrayOfNulls<Any?>(constructors[0].parameterCount)
    constructor.newInstance(*args) as T
}


inline fun <T> PropertyGraph.propertyRef(ref: KProperty0<T>): GraphProperty<T> = ref.toGraphProperty(this)

inline fun <T> PropertyGraph.propertyRef(ref: KMutableProperty0<T>): GraphProperty<T> = ref.toGraphProperty(this)

inline fun <T> PropertyGraph.propertyOf(initial: T): GraphProperty<T> = propertyOf { initial }

fun <T> PropertyGraph.propertyOf(initial: () -> T): GraphProperty<T> = try {
    // propertyBefore221(initial)
    graphProperty(initial)
} catch (e: Throwable) {
    propertyAfter221(initial)
}

@Suppress("UNCHECKED_CAST")
fun <T> PropertyGraph.propertyAfter221(initial: () -> T): GraphProperty<T> = run {
    val clazz = PropertyGraph::class.java

    val propertyMethod = clazz.methods.firstOrNull filter@{
        if (it.name != "lazyProperty") return@filter false
        if (it.parameterCount != 1) return@filter false

        val parameterTypes = it.parameterTypes
        if (!Function0::class.java.isAssignableFrom(parameterTypes[0])) return@filter false

        true
    } ?: throw NoSuchMethodException("PropertyGraph.lazyProperty no find")

    propertyMethod.invoke(this, initial) as GraphProperty<T>
}

@Suppress("UNCHECKED_CAST")
fun <T> PropertyGraph.propertyBefore221(initial: () -> T): GraphProperty<T> = run {
    val clazzCompanion = Class.forName("com.intellij.openapi.observable.properties.GraphPropertyImpl\$Companion")

    val propertyMethod = clazzCompanion.methods.firstOrNull filter@{
        if (it.name != "graphProperty") return@filter false
        if (it.parameterCount != 2) return@filter false

        val parameterTypes = it.parameterTypes
        if (!PropertyGraph::class.java.isAssignableFrom(parameterTypes[0])) return@filter false
        if (!Function0::class.java.isAssignableFrom(parameterTypes[1])) return@filter false

        true
    } ?: throw NoSuchMethodException("GraphPropertyImpl.Companion.graphProperty no find")

    val clazz = Class.forName("com.intellij.openapi.observable.properties.GraphPropertyImpl")
    val field = clazz.getDeclaredField("Companion")
    field.isAccessible = true
    val sCompanion = field.get(null)

    propertyMethod.invoke(sCompanion, this, initial) as GraphProperty<T>
}

inline var <T> GraphProperty<T>.value
    get() = get()
    set(value) = set(value)

inline fun <T> GraphProperty<T>.copyBind(
    parentDisposable: Disposable,
    propertyGraph: PropertyGraph = createPropertyGraph(),
): GraphProperty<T> = propertyGraph.propertyOf(::get).also { property ->
    property.afterChange({
        if (get() != it) set(it)
    }, parentDisposable)
}