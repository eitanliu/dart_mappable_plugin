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

    private var enableMixin by graph.enableMixin

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
                    .bindSelected(graph.enableMixin)
            }
            lateinit var customPredicate: ComponentPredicate
            row {
                val btn = radioButton("Custom", false)
                    .bindSelected(graph.enableMixin, true)
                customPredicate = btn.selected
            }
            indent {
                row {
                    checkBox("fromMap")
                        .bindSelected(graph.enableFromMap)
                    textField().apply {
                        bindText(graph.mappableFromMap)
                    }.horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                row {
                    checkBox("toMap")
                        .bindSelected(graph.enableToMap)
                    textField().apply {
                        bindText(graph.mappableToMap)
                    }.horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                row {
                    checkBox("fromJson")
                        .bindSelected(graph.enableFromJson)
                    textField().apply {
                        bindText(graph.mappableFromJson)
                    }.horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                row {
                    checkBox("toJson")
                        .bindSelected(graph.enableToJson)
                    textField().apply {
                        bindText(graph.mappableToJson)
                    }.horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                // row {
                //     checkBox("copyWith")
                //         .bindSelected(graph.enableCopyWith)
                //     textField().apply {
                //         bindText(graph.mappableCopyWith)
                //     }.horizontalAlign(HorizontalAlign.FILL)
                // }.layout(RowLayout.LABEL_ALIGNED)
            }.visibleIf(customPredicate)
        }.bind(::enableMixin)

        onApply(::apply)
    }

    override fun createComponent(): JComponent = rootPanel

    override fun isModified(): Boolean {
        return settings.graph.modelSuffix.value != graph.modelSuffix.value
                || settings.graph.enableMixin.value != graph.enableMixin.value
                || settings.graph.enableFromJson.value != graph.enableFromJson.value
                || settings.graph.enableToJson.value != graph.enableToJson.value
                || settings.graph.enableFromMap.value != graph.enableFromMap.value
                || settings.graph.enableToMap.value != graph.enableToMap.value
                || settings.graph.enableCopyWith.value != graph.enableCopyWith.value
                || settings.graph.mappableFromJson.value != graph.mappableFromJson.value
                || settings.graph.mappableToJson.value != graph.mappableToJson.value
                || settings.graph.mappableFromMap.value != graph.mappableFromMap.value
                || settings.graph.mappableToMap.value != graph.mappableToMap.value
                || settings.graph.mappableCopyWith.value != graph.mappableCopyWith.value
    }

    override fun apply() {
        settings.graph.modelSuffix.value = graph.modelSuffix.value
        settings.graph.enableMixin.value = graph.enableMixin.value
        settings.graph.enableFromJson.value = graph.enableFromJson.value
        settings.graph.enableToJson.value = graph.enableToJson.value
        settings.graph.enableFromMap.value = graph.enableFromMap.value
        settings.graph.enableToMap.value = graph.enableToMap.value
        settings.graph.enableCopyWith.value = graph.enableCopyWith.value
        settings.graph.mappableFromJson.value = graph.mappableFromJson.value
        settings.graph.mappableToJson.value = graph.mappableToJson.value
        settings.graph.mappableFromMap.value = graph.mappableFromMap.value
        settings.graph.mappableToMap.value = graph.mappableToMap.value
        settings.graph.mappableCopyWith.value = graph.mappableCopyWith.value
    }

    override fun reset() {
        graph.modelSuffix.value = settings.graph.modelSuffix.value
        graph.enableMixin.value = settings.graph.enableMixin.value
        graph.enableFromJson.value = settings.graph.enableFromJson.value
        graph.enableToJson.value = settings.graph.enableToJson.value
        graph.enableFromMap.value = settings.graph.enableFromMap.value
        graph.enableToMap.value = settings.graph.enableToMap.value
        graph.enableCopyWith.value = settings.graph.enableCopyWith.value
        graph.mappableFromJson.value = settings.graph.mappableFromJson.value
        graph.mappableToJson.value = settings.graph.mappableToJson.value
        graph.mappableFromMap.value = settings.graph.mappableFromMap.value
        graph.mappableToMap.value = settings.graph.mappableToMap.value
        graph.mappableCopyWith.value = settings.graph.mappableCopyWith.value
    }

    class Graph(data: SettingLayout) {
        private val propertyGraph = PropertyGraph()

        val modelSuffix = propertyGraph.propertyOf(data.settings.modelSuffix)
        val enableMixin = propertyGraph.propertyOf(data.settings.enableMixin)
        val enableFromJson = propertyGraph.propertyOf(data.settings.enableFromJson)
        val enableToJson = propertyGraph.propertyOf(data.settings.enableToJson)
        val enableFromMap = propertyGraph.propertyOf(data.settings.enableFromMap)
        val enableToMap = propertyGraph.propertyOf(data.settings.enableToMap)
        val enableCopyWith = propertyGraph.propertyOf(data.settings.enableCopyWith)
        val mappableFromJson = propertyGraph.propertyOf(data.settings.mappableFromJson)
        val mappableToJson = propertyGraph.propertyOf(data.settings.mappableToJson)
        val mappableFromMap = propertyGraph.propertyOf(data.settings.mappableFromMap)
        val mappableToMap = propertyGraph.propertyOf(data.settings.mappableToMap)
        val mappableCopyWith = propertyGraph.propertyOf(data.settings.mappableCopyWith)

    }
}
