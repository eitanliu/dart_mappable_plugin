@file:Suppress("DialogTitleCapitalization")

package com.eitanliu.dart.mappable.ui

import com.eitanliu.dart.mappable.ast.DartGenerator
import com.eitanliu.dart.mappable.binding.bind
import com.eitanliu.dart.mappable.binding.bindTabTransferFocus
import com.eitanliu.dart.mappable.extensions.copyBind
import com.eitanliu.dart.mappable.extensions.createPropertyGraph
import com.eitanliu.dart.mappable.extensions.propertyRef
import com.eitanliu.dart.mappable.extensions.value
import com.eitanliu.dart.mappable.generator.buildDartGenerator
import com.eitanliu.dart.mappable.observable.PropertyGraphWrapper
import com.eitanliu.dart.mappable.settings.Settings
import com.google.gson.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.layout.applyToComponent
import com.intellij.ui.layout.panel
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
            right {
                button("Format") {
                    handleFormatJSONString()
                    myPreferredFocusedComponent?.requestFocus()
                }.apply {
                    // constraints(growX)
                    // growPolicy(GrowPolicy.MEDIUM_TEXT)
                }
            }
        }
        row {
            scrollPane(
                JBTextArea().apply {
                    rows = 10
                    myPreferredFocusedComponent = this
                    bind(graph.json)
                    bindTabTransferFocus()
                    // constraints(pushX, pushY)
                }
            )
        }
        row { label("Class Name:") }
        row {
            textField(graph.className).apply {
                constraints(pushX)
            }
        }
        row {

            checkBox(
                "constructor", graph.constructor
            ).apply {
                constraints(pushX)
            }.applyToComponent {
                toolTipText = "constructor has params"
            }

            checkBox(
                "nullable", graph.nullable
            ).apply {
                constraints(pushX)
            }.applyToComponent {
                toolTipText = "members is nullable"
            }

            checkBox(
                "final", graph.final
            ).apply {
                constraints(pushX)
            }.applyToComponent {
                toolTipText = "members is final"
            }

            right {
                button("Settings") {
                    SettingsDialog(null, contentPanel).show()
                }
            }
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

        val g = buildDartGenerator(settings, className, json)

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
        private val propertyGraph: PropertyGraph = createPropertyGraph()
        private val settings = data.settings
        private val disposable = data.disposable

        val className = propertyGraph.propertyRef(data::className)
        val json = propertyGraph.propertyRef(data::json)

        val constructor = settings.graph.constructor.copyBind(disposable, propertyGraph)
        val nullable = settings.graph.nullable.copyBind(disposable, propertyGraph)
        val final = settings.graph.final.copyBind(disposable, propertyGraph)

        fun afterPropagation(disposable: Disposable? = null, listener: Graph.() -> Unit) = apply {
            propertyGraph.afterPropagation { listener() }
        }
    }
}