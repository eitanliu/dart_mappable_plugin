package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.binding.bindSelected
import com.eitanliu.dart.mappable.binding.selected
import com.eitanliu.intellij.compat.extensions.createPropertyGraph
import com.eitanliu.intellij.compat.extensions.propertyOf
import com.eitanliu.intellij.compat.extensions.value
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.options.UnnamedConfigurable
import com.intellij.ui.TitledSeparator
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent


@Suppress("DialogTitleCapitalization")
class SettingLayout(private val settings: Settings) : UnnamedConfigurable {
    val graph = Graph(this)

    private var implement by graph.implement
    private var enableMixin by graph.enableMixin

    val rootPanel = panel {

        onApply(::apply)

        row {
            label("Model suffix:").align(AlignX.LEFT)

            textField().apply {
                bindText(graph.modelSuffix)
            }.align(AlignX.FILL)

            rowComment("Configure dart data model files suffix.")
        }
        row {
            checkBox(
                "auto run build_runner"
            ).bindSelected(
                graph.autoBuildRunner
            ).applyToComponent {
                toolTipText = "auto run 'flutter pub run build_runner build --delete-conflicting-outputs'"
            }
        }
        buttonsGroup("Implement") {
            row {
                checkBox(
                    "json_reflectable"
                ).bindSelected(
                    graph.enableJsonReflectable
                ).applyToComponent {
                    toolTipText = "use json_reflectable annotation"
                }
            }
            row {
                radioButton("json_serializable", Implements.JSON_SERIALIZABLE)
                    .bindSelected(graph.implement, Implements.JSON_SERIALIZABLE)
            }
            val mappablePredicate = graph.implement.selected(Implements.DART_MAPPABLE)
            row {
                radioButton("dart_mappable", Implements.DART_MAPPABLE)
                    .bindSelected(graph.implement, Implements.DART_MAPPABLE)
            }
            // panel {
            rowsRange {
                buildMappable()
            }.visibleIf(mappablePredicate)
            val freezedPredicate = graph.implement.selected(Implements.FREEZED)
            row {
                radioButton("freezed", Implements.FREEZED)
                    .bindSelected(graph.implement, Implements.FREEZED)
            }
            rowsRange {
                buildFreezed()
            }.visibleIf(freezedPredicate)
        }.bind(::implement)

    }

    private fun Panel.buildMappable() {
        buttonsGroup(indent = true) {
            val customPredicate = graph.enableMixin.selected(false)
            row {
                radioButton("Mixin", true)
                    .bindSelected(graph.enableMixin, true)
            }
            row {
                radioButton("Custom", false)
                    .bindSelected(graph.enableMixin, false)
            }
            rowPanel(indent = true) {
                row {
                    checkBox("fromMap")
                        .bindSelected(graph.enableFromMap)
                    textField().apply {
                        bindText(graph.mappableFromMap)
                    }.align(AlignX.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                row {
                    checkBox("toMap")
                        .bindSelected(graph.enableToMap)
                    textField().apply {
                        bindText(graph.mappableToMap)
                    }.align(AlignX.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                row {
                    checkBox("fromJson")
                        .bindSelected(graph.enableFromJson)
                    textField().apply {
                        bindText(graph.mappableFromJson)
                    }.align(AlignX.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                row {
                    checkBox("toJson")
                        .bindSelected(graph.enableToJson)
                    textField().apply {
                        bindText(graph.mappableToJson)
                    }.align(AlignX.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
                row {
                    checkBox("copyWith")
                        .bindSelected(graph.enableCopyWith)
                    textField().apply {
                        bindText(graph.mappableCopyWith)
                    }.align(AlignX.FILL)
                }.layout(RowLayout.LABEL_ALIGNED)
            }.visibleIf(customPredicate)
        }.bind(::enableMixin)
    }

    private fun Panel.buildFreezed() {
        indent {
            row {
                checkBox("fromJson/toJson")
                    .bindSelected(graph.freezedEnableJson)
            }
        }
    }

    private fun Panel.rowPanel(
        title: String? = null,
        indent: Boolean = true,
        init: Panel.() -> Unit
    ) = row {
        panel {
            if (title != null) row { cell(TitledSeparator(title)) }
            if (indent) {
                indent(init)
            } else {
                init()
            }
        }
    }

    override fun createComponent(): JComponent = rootPanel

    override fun isModified(): Boolean {
        return settings.graph.modelSuffix.value != graph.modelSuffix.value
                || settings.graph.implement.value != graph.implement.value
                || settings.graph.autoBuildRunner.value != graph.autoBuildRunner.value
                || settings.graph.enableJsonReflectable.value != graph.enableJsonReflectable.value
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
                || settings.graph.freezedEnableJson.value != graph.freezedEnableJson.value
    }

    override fun apply() {
        settings.graph.modelSuffix.value = graph.modelSuffix.value
        settings.graph.implement.value = graph.implement.value
        settings.graph.autoBuildRunner.value = graph.autoBuildRunner.value
        settings.graph.enableJsonReflectable.value = graph.enableJsonReflectable.value
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
        settings.graph.freezedEnableJson.value = graph.freezedEnableJson.value
    }

    override fun reset() {
        graph.modelSuffix.value = settings.graph.modelSuffix.value
        graph.implement.value = settings.graph.implement.value
        graph.autoBuildRunner.value = settings.graph.autoBuildRunner.value
        graph.enableJsonReflectable.value = settings.graph.enableJsonReflectable.value
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
        graph.freezedEnableJson.value = settings.graph.freezedEnableJson.value
    }

    class Graph(data: SettingLayout) {
        private val propertyGraph: PropertyGraph = createPropertyGraph()

        val modelSuffix = propertyGraph.propertyOf(data.settings.modelSuffix)
        val implement = propertyGraph.propertyOf(data.settings.implement)
        val autoBuildRunner = propertyGraph.propertyOf(data.settings.autoBuildRunner)
        val enableJsonReflectable = propertyGraph.propertyOf(data.settings.enableJsonReflectable)
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
        val freezedEnableJson = propertyGraph.propertyOf(data.settings.freezedEnableJson)

    }
}
