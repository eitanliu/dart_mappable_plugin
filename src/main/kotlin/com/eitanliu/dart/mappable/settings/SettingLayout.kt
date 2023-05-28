package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.binding.bindSelected
import com.eitanliu.dart.mappable.extensions.propertyOf
import com.eitanliu.dart.mappable.extensions.value
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.options.UnnamedConfigurable
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.layout.ComponentPredicate
import javax.swing.JComponent


@Suppress("DialogTitleCapitalization")
class SettingLayout(private val settings: Settings) : UnnamedConfigurable {
    val graph = Graph(this)

    private var mappableMixin by graph.mappableMixin

    val rootPanel = panel {
        row {
            label("Model suffix:").horizontalAlign(HorizontalAlign.LEFT)

            textField().apply {
                bindText(graph.modelSuffix)
            }.horizontalAlign(HorizontalAlign.FILL)

            rowComment("Configure dart data model files suffix.")
        }
        separator("Mappable")
        buttonsGroup("Implement:", indent = false) {
            row {
                radioButton("Mixin", true)
                    .bindSelected(graph.mappableMixin)
            }
            lateinit var customPredicate: ComponentPredicate
            row {
                val btn = radioButton("Custom", false)
                    .bindSelected(graph.mappableMixin, true)
                customPredicate = btn.selected
            }
            indent {
                row {
                    checkBox("copyWith")
                        .bindSelected(graph.mappableCopyWith)
                }
                row {
                    label("fromMap")
                    textField().apply {
                        bindText(graph.mappableFromMap)
                    }.horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                row {
                    label("toMap")
                    textField().apply {
                        bindText(graph.mappableToMap)
                    }.horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                row {
                    label("fromJson")
                    textField().apply {
                        bindText(graph.mappableFromJson)
                    }.horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                row {
                    label("toJson")
                    textField().apply {
                        bindText(graph.mappableToJson)
                    }.horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
            }.visibleIf(customPredicate)
        }.bind(::mappableMixin)

        onApply(::apply)
    }

    override fun createComponent(): JComponent = rootPanel

    override fun isModified(): Boolean {
        return settings.graph.modelSuffix.value != graph.modelSuffix.value
                || settings.graph.mappableMixin.value != graph.mappableMixin.value
                || settings.graph.mappableCopyWith.value != graph.mappableCopyWith.value
                || settings.graph.mappableFromJson.value != graph.mappableFromJson.value
                || settings.graph.mappableToJson.value != graph.mappableToJson.value
                || settings.graph.mappableFromMap.value != graph.mappableFromMap.value
                || settings.graph.mappableToMap.value != graph.mappableToMap.value
    }

    override fun apply() {
        settings.graph.modelSuffix.value = graph.modelSuffix.value
        settings.graph.mappableMixin.value = graph.mappableMixin.value
        settings.graph.mappableCopyWith.value = graph.mappableCopyWith.value
        settings.graph.mappableFromJson.value = graph.mappableFromJson.value
        settings.graph.mappableToJson.value = graph.mappableToJson.value
        settings.graph.mappableFromMap.value = graph.mappableFromMap.value
        settings.graph.mappableToMap.value = graph.mappableToMap.value
    }

    override fun reset() {
        graph.modelSuffix.value = settings.graph.modelSuffix.value
        graph.mappableMixin.value = settings.graph.mappableMixin.value
        graph.mappableCopyWith.value = settings.graph.mappableCopyWith.value
        graph.mappableFromJson.value = settings.graph.mappableFromJson.value
        graph.mappableToJson.value = settings.graph.mappableToJson.value
        graph.mappableFromMap.value = settings.graph.mappableFromMap.value
        graph.mappableToMap.value = settings.graph.mappableToMap.value
    }

    class Graph(data: SettingLayout) {
        private val propertyGraph = PropertyGraph()

        val modelSuffix = propertyGraph.propertyOf(data.settings.modelSuffix)
        val mappableMixin = propertyGraph.propertyOf(data.settings.mappableMixin)
        val mappableCopyWith = propertyGraph.propertyOf(data.settings.mappableCopyWith)
        val mappableFromJson = propertyGraph.propertyOf(data.settings.mappableFromJson)
        val mappableToJson = propertyGraph.propertyOf(data.settings.mappableToJson)
        val mappableFromMap = propertyGraph.propertyOf(data.settings.mappableFromMap)
        val mappableToMap = propertyGraph.propertyOf(data.settings.mappableToMap)

    }
}
