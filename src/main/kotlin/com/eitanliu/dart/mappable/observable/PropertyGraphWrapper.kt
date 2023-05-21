package com.eitanliu.dart.mappable.observable

import com.eitanliu.dart.mappable.extensions.WeakRecFun1List
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.util.containers.WeakList
import java.lang.ref.WeakReference

class PropertyGraphWrapper<T>(
    propertyGraph: PropertyGraph,
    graph: T, list: WeakList<T.() -> Unit> = WeakList()
) {
    val refPropertyGraph = WeakReference(propertyGraph)

    val listenerList: WeakRecFun1List<T> = WeakRecFun1List(graph, list)

    val listener = listenerList.listener

    val refGraph = listenerList.weakRef
}