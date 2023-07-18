package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.binding.bindSelected
import com.eitanliu.dart.mappable.binding.selected
import com.eitanliu.dart.mappable.extensions.createPropertyGraph
import com.eitanliu.dart.mappable.extensions.propertyOf
import com.eitanliu.dart.mappable.extensions.value
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.options.UnnamedConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.layout.*
import javax.swing.ButtonGroup
import javax.swing.JComponent


@Suppress("DialogTitleCapitalization")
class SettingLayout(private val settings: Settings) : UnnamedConfigurable {
    val graph = Graph(this)

    private var implement by graph.implement
    private var enableMixin by graph.enableMixin

    val rootPanel = panel {
        row {
            label("Model suffix:")

            textField(graph.modelSuffix).apply {
                constraints(pushX)
            }
        }
        row { label("Configure dart data model files suffix.") }
        row {
            checkBox(
                "auto run build_runner", graph.autoBuildRunner
            ).applyToComponent {
                toolTipText = "auto run 'flutter pub run build_runner build --delete-conflicting-outputs'"
            }
        }
        row("Implement") {
            subRowIndent = 1
            val group = ButtonGroup()
            row {
                checkBox(
                    "json_reflectable", graph.enableJsonReflectable
                ).applyToComponent {
                    toolTipText = "use json_reflectable annotation"
                }
            }
            row {
                radioButton("json_serializable")
                    .bindSelected(graph.implement, Implements.JSON_SERIALIZABLE, group)
            }

            val mappablePredicate = graph.implement.selected(Implements.DART_MAPPABLE)
            row {
                radioButton("dart_mappable")
                    .bindSelected(graph.implement, Implements.DART_MAPPABLE, group)
            }
            // panel {
            row {
                buildMappable(mappablePredicate)
            }

            val freezedPredicate = graph.implement.selected(Implements.FREEZED)
            row {
                radioButton("freezed")
                    .bindSelected(graph.implement, Implements.FREEZED, group)
            }
            // panel {
            row {
                buildFreezed(freezedPredicate)
            }
        }

    }

    private fun Row.buildMappable(visibility: ComponentPredicate? = null) = nestedPanel(null, visibility) {
        row {
            subRowIndent = 1
            val customPredicate = graph.enableMixin.selected(false)

            val group = ButtonGroup()
            row {
                radioButton("Mixin")
                    .bindSelected(graph.enableMixin, true, group)
            }
            row {
                radioButton("Custom")
                    .bindSelected(graph.enableMixin, false, group)
            }
            rowRanger(visibility = customPredicate) {
                subRowIndent = 2
                row {
                    checkBox("fromMap", graph.enableFromMap)
                    textField(graph.mappableFromMap).apply {
                        constraints(pushX)
                    }
                }
                row {
                    checkBox("toMap", graph.enableToMap)
                    textField(graph.mappableToMap).apply {
                        constraints(pushX)
                    }
                }
                row {
                    checkBox("fromJson", graph.enableFromJson)
                    textField(graph.mappableFromJson).apply {
                        constraints(pushX)
                    }
                }
                row {
                    checkBox("toJson", graph.enableToJson)
                    textField(graph.mappableToJson).apply {
                        constraints(pushX)
                    }
                }
                row {
                    checkBox("copyWith", graph.enableCopyWith)
                    textField(graph.mappableCopyWith).apply {
                        constraints(pushX)
                    }
                }
            }
        }

    }


    private fun Row.buildFreezed(visibility: ComponentPredicate? = null) = nestedPanel(null, visibility) {
        row {
            subRowIndent = 2
            row {
                checkBox("fromJson/toJson", graph.freezedEnableJson)
            }
        }
    }

    private fun LayoutBuilder.rowRanger(
        title: String? = null,
        visibility: ComponentPredicate? = null,
        init: Row.() -> Unit
    ) = row {
        nestedPanelRow(title, visibility, init)
    }


    private fun Row.nestedPanelRow(
        title: String? = null,
        visibility: ComponentPredicate? = null,
        init: Row.() -> Unit
    ) = nestedPanel(title, visibility) {
        row { init() }
    }

    fun Row.nestedPanel(
        title: String? = null,
        visibility: ComponentPredicate? = null,
        init: LayoutBuilder.() -> Unit
    ): CellBuilder<DialogPanel> {
        return component(com.intellij.ui.layout.panel(title = title) {
            // if (title != null) separator(title)
            init()
        }).apply {
            if (visibility != null) visibleIf(visibility)
        }
    }

    override fun createComponent(): JComponent = JBScrollPane(rootPanel).apply {
        horizontalScrollBar = null
        border = null
    }

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
