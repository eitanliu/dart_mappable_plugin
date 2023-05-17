package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.extensions.propertyOf
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
    var factory: Boolean,
    var nullable: Boolean,
) : PersistentStateComponent<Settings> {

    val graph = Graph(this)

    constructor() : this(
        modelSuffix = "entity", ensureInitialized = true,
        constructor = true, factory = true,
        nullable = false
    )

    override fun getState(): Settings {
        return this
    }

    override fun loadState(state: Settings) {

        XmlSerializerUtil.copyBean(state, this)
    }

    class Graph(private val data: Settings) {
        private val propertyGraph = PropertyGraph()

        val modelSuffix = propertyGraph.propertyOf(data::modelSuffix)

        val ensureInitialized = propertyGraph.propertyOf(data::ensureInitialized)
        val constructor = propertyGraph.propertyOf(data::constructor)
        val factory = propertyGraph.propertyOf(data::factory)
        val nullable = propertyGraph.propertyOf(data::nullable)
    }
}