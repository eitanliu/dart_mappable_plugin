package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.extensions.propertyRef
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "DartMappableSettings", storages = [(Storage("DartMappableSettings.xml"))])
data class Settings(
    var modelSuffix: String,
    var ensureInitialized: Boolean,
    var constructor: Boolean,
    var nullable: Boolean,
    var final: Boolean,
) : PersistentStateComponent<Settings> {

    val graph = Graph(this)

    constructor() : this(
        modelSuffix = "entity", ensureInitialized = true,
        constructor = true, nullable = false,
        final = false,
    )

    override fun getState(): Settings {
        return this
    }

    override fun loadState(state: Settings) {

        XmlSerializerUtil.copyBean(state, this)
    }

    class Graph(private val data: Settings) {
        private val propertyGraph = PropertyGraph()

        val modelSuffix = propertyGraph.propertyRef(data::modelSuffix)

        val ensureInitialized = propertyGraph.propertyRef(data::ensureInitialized)
        val constructor = propertyGraph.propertyRef(data::constructor)
        val nullable = propertyGraph.propertyRef(data::nullable)
        val final = propertyGraph.propertyRef(data::final)

        fun afterPropagation(disposable: Disposable? = null, listener: Graph.() -> Unit) = apply {
            propertyGraph.afterPropagation(disposable) { listener() }
        }
    }
}