package com.eitanliu.dart.mappable.observable

import com.eitanliu.compat.ref.WeakFun1List
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.util.containers.WeakList
import java.lang.ref.WeakReference

class PropertyGraphWrapper<T>(
    propertyGraph: PropertyGraph,
    graph: T, list: WeakList<T.() -> Unit> = WeakList()
) {
    val refPropertyGraph = WeakReference(propertyGraph)

    val listenerList: WeakFun1List<T> = WeakFun1List(graph, list)

    val listener = listenerList.weakFun

    val refGraph = listenerList.receiver
}