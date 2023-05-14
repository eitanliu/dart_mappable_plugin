package com.eitanliu.dart_mappable.settings

import com.eitanliu.dart_mappable.extensions.toGraphProperty
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.GraphPropertyImpl
import com.intellij.openapi.observable.properties.GraphPropertyImpl.Companion.graphProperty
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

    private val propertyGraph = PropertyGraph()

    val modelSuffixProperty = ::modelSuffix.toGraphProperty(propertyGraph)

    val ensureInitializedProperty = ::ensureInitialized.toGraphProperty(propertyGraph)
    val constructorProperty = ::constructor.toGraphProperty(propertyGraph)
    val factoryProperty = ::factory.toGraphProperty(propertyGraph)
    val nullableProperty = ::nullable.toGraphProperty(propertyGraph)

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
}