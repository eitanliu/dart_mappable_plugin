@file:Suppress("DialogTitleCapitalization")

package com.eitanliu.dart.mappable.ui

import com.eitanliu.dart.mappable.ast.DartGenerator
import com.eitanliu.dart.mappable.binding.bindTabTransferFocus
import com.eitanliu.dart.mappable.extensions.copyBind
import com.eitanliu.dart.mappable.extensions.propertyRef
import com.eitanliu.dart.mappable.extensions.value
import com.eitanliu.dart.mappable.generator.DartMappableGenerator
import com.eitanliu.dart.mappable.settings.Settings
import com.google.gson.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import com.intellij.util.ui.JBDimension

/**
 * Json input Dialog
 */
class JsonInputDialog(
    project: Project,
    private val className: String = "",
    private val json: String = "",
    val doOkAction: (generator: DartGenerator) -> Boolean
) : DialogWrapper(
    project, true,
) {
    var generator: DartGenerator? = null
        private set

    val settings = ApplicationManager.getApplication().getService(Settings::class.java)

    val graph = Graph(this).afterPropagation(disposable) {
        okAction.isEnabled = inputIsValidJson(json.value).takeIf {
            className.value.trim().isNotEmpty() && json.value.trim().isNotEmpty()
        } ?: false
    }

    private val prettyGson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

    init {
        title = "Generate Dart Data Class Code"
        setOKButtonText("Generate")
        okAction.apply {
            putValue(DEFAULT_ACTION, false)
            isEnabled = className.trim().isNotEmpty() && inputIsValidJson(json)
        }
        init()
    }

    override fun createCenterPanel() = panel {

        row {
            label("Please input the class name and JSON String for generating dart data class")
        }
        row {
            label("JSON Text:")
            button("Format") {
                handleFormatJSONString()
                myPreferredFocusedComponent?.requestFocus()
            }.horizontalAlign(HorizontalAlign.RIGHT)
        }
        row {
            resizableRow()
            textArea().apply {
                bindText(graph.json)
                applyToComponent {
                    rows = 10
                    myPreferredFocusedComponent = this
                    bindTabTransferFocus()
                }
                horizontalAlign(HorizontalAlign.FILL)
                verticalAlign(VerticalAlign.FILL)
            }
        }
        row { label("Class Name:") }
        row {
            textField().apply {
                bindText(graph.className)
                horizontalAlign(HorizontalAlign.FILL)
            }
        }
        row {

            checkBox(
                "constructor"
            ).bindSelected(
                graph.constructor
            ).applyToComponent {
                toolTipText = "constructor has params"
            }

            checkBox(
                "nullable"
            ).bindSelected(
                graph.nullable
            ).applyToComponent {
                toolTipText = "members is nullable"
            }

            checkBox(
                "final"
            ).bindSelected(
                graph.final
            ).applyToComponent {
                toolTipText = "members is final"
            }

            button("Settings") {
                SettingsDialog(null, contentPanel).show()
            }.horizontalAlign(HorizontalAlign.RIGHT)
        }

    }.apply {
        preferredSize = JBDimension(600, 500)
    }

    private fun handleFormatJSONString() {
        val currentText = graph.json.value
        if (currentText.isNotEmpty()) {
            try {
                val jsonElement = prettyGson.fromJson(currentText, JsonElement::class.java)
                val formatJSON = prettyGson.toJson(jsonElement)
                graph.json.value = formatJSON
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun inputIsValidJson(string: String) = try {
        val jsonElement = JsonParser.parseString(string)
        (jsonElement.isJsonObject || jsonElement.isJsonArray)
    } catch (e: JsonSyntaxException) {
        false
    }

    override fun doOKAction() {
        val className = graph.className.value
        val json = graph.json.value

        if (className.isEmpty()) {
            Messages.showErrorDialog("Error", "className must not null or empty")
            return
        }

        if (json.isEmpty()) {
            Messages.showErrorDialog("Error", "json must not null or empty")
            return
        }

        val g = DartMappableGenerator(settings, className, json)

        if (doOkAction(g)) {
            generator = g
            super.doOKAction()
        }
    }

    fun showDialog(): JsonInputDialog {
        show()
        return this
    }

    class Graph(private val data: JsonInputDialog) {
        private val propertyGraph = PropertyGraph()
        private val settings = data.settings
        private val disposable = data.disposable

        val className = propertyGraph.propertyRef(data::className)
        val json = propertyGraph.propertyRef(data::json)

        val constructor = settings.graph.constructor.copyBind(disposable, propertyGraph)
        val nullable = settings.graph.nullable.copyBind(disposable, propertyGraph)
        val final = settings.graph.final.copyBind(disposable, propertyGraph)

        fun afterPropagation(disposable: Disposable? = null, listener: Graph.() -> Unit) = apply {
            propertyGraph.afterPropagation(disposable) { listener() }
        }
    }
}