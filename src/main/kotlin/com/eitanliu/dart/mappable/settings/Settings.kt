package com.eitanliu.dart.mappable.settings

import com.eitanliu.intellij.compat.extensions.createPropertyGraph
import com.eitanliu.intellij.compat.extensions.propertyRef
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "DartMappableSettings", storages = [(Storage("DartMappableSettings.xml"))])
data class Settings(
    var modelSuffix: String,
    var implement: String,
    var constructor: Boolean,
    var nullable: Boolean,
    var final: Boolean,
    var autoBuildRunner: Boolean,
    var enableJsonReflectable: Boolean,
    var enableMixin: Boolean,
    var enableFromJson: Boolean,
    var enableToJson: Boolean,
    var enableFromMap: Boolean,
    var enableToMap: Boolean,
    var enableCopyWith: Boolean,
    var mappableFromJson: String,
    var mappableToJson: String,
    var mappableFromMap: String,
    var mappableToMap: String,
    var mappableCopyWith: String,
    var freezedEnableJson: Boolean,
) : PersistentStateComponent<Settings> {

    val graph = Graph(this)

    constructor() : this(
        modelSuffix = "entity", implement = Implements.JSON_SERIALIZABLE,
        constructor = true, nullable = false, final = false,
        autoBuildRunner = true,
        enableJsonReflectable = false,
        enableMixin = true,
        enableFromJson = true, enableToJson = true,
        enableFromMap = true, enableToMap = true,
        enableCopyWith = true,
        mappableFromJson = "fromString", mappableToJson = "toString",
        mappableFromMap = "fromJson", mappableToMap = "toJson",
        mappableCopyWith = "copyWith",
        freezedEnableJson = true,
    )

    override fun getState(): Settings {
        return this
    }

    override fun loadState(state: Settings) {

        XmlSerializerUtil.copyBean(state, this)
    }

    class Graph(private val data: Settings) {
        private val propertyGraph: PropertyGraph = createPropertyGraph()

        val modelSuffix = propertyGraph.propertyRef(data::modelSuffix)
        val implement = propertyGraph.propertyRef(data::implement)

        val constructor = propertyGraph.propertyRef(data::constructor)
        val nullable = propertyGraph.propertyRef(data::nullable)
        val final = propertyGraph.propertyRef(data::final)

        val autoBuildRunner = propertyGraph.propertyRef(data::autoBuildRunner)
        val enableJsonReflectable = propertyGraph.propertyRef(data::enableJsonReflectable)

        val enableMixin = propertyGraph.propertyRef(data::enableMixin)
        val enableFromJson = propertyGraph.propertyRef(data::enableFromJson)
        val enableToJson = propertyGraph.propertyRef(data::enableToJson)
        val enableFromMap = propertyGraph.propertyRef(data::enableFromMap)
        val enableToMap = propertyGraph.propertyRef(data::enableToMap)
        val enableCopyWith = propertyGraph.propertyRef(data::enableCopyWith)

        val mappableFromJson = propertyGraph.propertyRef(data::mappableFromJson)
        val mappableToJson = propertyGraph.propertyRef(data::mappableToJson)
        val mappableFromMap = propertyGraph.propertyRef(data::mappableFromMap)
        val mappableToMap = propertyGraph.propertyRef(data::mappableToMap)
        val mappableCopyWith = propertyGraph.propertyRef(data::mappableCopyWith)

        val freezedEnableJson = propertyGraph.propertyRef(data::freezedEnableJson)

        fun afterPropagation(disposable: Disposable? = null, listener: Graph.() -> Unit) = apply {
            propertyGraph.afterPropagation(disposable) { listener() }
        }
    }
}