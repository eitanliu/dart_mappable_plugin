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
    var mappableMixin: Boolean,
    var mappableCopyWith: Boolean,
    var mappableFromJson: String,
    var mappableToJson: String,
    var mappableFromMap: String,
    var mappableToMap: String,
) : PersistentStateComponent<Settings> {

    val graph = Graph(this)

    constructor() : this(
        modelSuffix = "entity", ensureInitialized = true,
        constructor = true, nullable = false, final = false,
        mappableMixin = true, mappableCopyWith = true,
        mappableFromJson = "fromString", mappableToJson = "toString",
        mappableFromMap = "fromJson", mappableToMap = "toJson",
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

        val mappableMixin = propertyGraph.propertyRef(data::mappableMixin)
        val mappableCopyWith = propertyGraph.propertyRef(data::mappableCopyWith)
        val mappableFromJson = propertyGraph.propertyRef(data::mappableFromJson)
        val mappableToJson = propertyGraph.propertyRef(data::mappableToJson)
        val mappableFromMap = propertyGraph.propertyRef(data::mappableFromMap)
        val mappableToMap = propertyGraph.propertyRef(data::mappableToMap)

        fun afterPropagation(disposable: Disposable? = null, listener: Graph.() -> Unit) = apply {
            propertyGraph.afterPropagation(disposable) { listener() }
        }
    }
}