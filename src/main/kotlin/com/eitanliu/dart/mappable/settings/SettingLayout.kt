package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.binding.bindSelected
import com.eitanliu.dart.mappable.binding.selected
import com.eitanliu.dart.mappable.extensions.createInstance
import com.eitanliu.dart.mappable.extensions.createPropertyGraph
import com.eitanliu.dart.mappable.extensions.propertyOf
import com.eitanliu.dart.mappable.extensions.value
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.options.UnnamedConfigurable
import com.intellij.ui.TitledSeparator
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.JComponent


@Suppress("DialogTitleCapitalization", "MemberVisibilityCanBePrivate")
class SettingLayout(private val settings: Settings) : UnnamedConfigurable {
    val graph = Graph(this)

    internal var implement by graph.implement
    internal var enableMixin by graph.enableMixin

    val rootPanel = panel {

        onApply(::apply)

        row {
            label("Model suffix:").horizontalAlign(HorizontalAlign.LEFT)

            textField().apply {
                bindText(graph.modelSuffix)
            }.horizontalAlign(HorizontalAlign.FILL)

            rowComment("Configure dart data model files suffix.")
        }
        buttonGroup(graph.implement, "Implement") {
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
        }

    }

    private fun Panel.buildMappable() {
        buttonGroup(graph.enableMixin, indent = true) {
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
        }
    }

    // buttonGroup removed 231
    inline fun <reified T : Any> Panel.buttonGroup(
        binding: GraphProperty<T>, title: String? = null,
        indent: Boolean = title != null,
        noinline init: Panel.() -> Unit
    ) {
        val methods = this.javaClass.methods
        // val buttonGroup = methods.filter { it.name == "buttonGroup" }
        // println("buttonGroup methods ${buttonGroup.joinToString { "${it.parameterCount}" }}")
        // val buttonsGroup = this.javaClass.methods.filter { it.name == "buttonsGroup" }
        // println("buttonsGroup methods ${buttonsGroup.joinToString { "${it.parameterCount}" }}")
        // buttonsGroup(title, indent, init).bind(binding::get, binding::set)
        try {
            methods.firstOrNull { it.name == "buttonGroup" && it.parameterCount == 5 }?.also { method ->
                val property = Class.forName("com.intellij.ui.layout.PropertyBinding")
                    .createInstance { arrayOf(binding::get, binding::set) }
                method.invoke(this, property, T::class.java, title, indent, init)
            } ?: methods.firstOrNull { it.name == "buttonsGroup" && it.parameterCount == 3 }?.also { method ->
                val group = method.invoke(this, title, indent, init)
                val bind = group?.run {
                    javaClass.methods.firstOrNull {
                        it.name == "bind"
                    }
                }
                val clazz = Class.forName("com.intellij.ui.dsl.builder.MutablePropertyKt")
                val propertyMethod = clazz.methods.firstOrNull { it.name == "MutableProperty" }
                val property = propertyMethod?.invoke(null, binding::get, binding::set)
                bind?.invoke(group, property, T::class.java)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
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
    }

    override fun apply() {
        settings.graph.modelSuffix.value = graph.modelSuffix.value
        settings.graph.implement.value = graph.implement.value
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
    }

    override fun reset() {
        graph.modelSuffix.value = settings.graph.modelSuffix.value
        graph.implement.value = settings.graph.implement.value
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
    }

    class Graph(data: SettingLayout) {
        private val propertyGraph: PropertyGraph = createPropertyGraph()

        val modelSuffix = propertyGraph.propertyOf(data.settings.modelSuffix)
        val implement = propertyGraph.propertyOf(data.settings.implement)
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

    }
}
