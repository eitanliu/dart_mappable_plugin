@file:Suppress("DialogTitleCapitalization")

package com.eitanliu.dart_mappable.ui

import com.eitanliu.dart_mappable.extensions.propertyOf
import com.eitanliu.dart_mappable.extensions.value
import com.eitanliu.dart_mappable.settings.Settings
import com.eitanliu.dart_mappable.utils.SimpleKeyListener
import com.google.gson.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import com.intellij.util.ui.JBDimension
import java.awt.event.KeyEvent

/**
 * Json input Dialog
 */
open class JsonInputDialog(
    project: Project,
    private var className: String = "",
    private var json: String = "",
    val doOkAction: (className: String, json: String) -> Boolean
) : DialogWrapper(
    project, true,
) {

    val graph = Graph(this).afterPropagation {
        okAction.isEnabled = if (className.value.trim().isNotEmpty() && json.value.trim().isNotEmpty()) {
            inputIsValidJson(json.value)
        } else {
            false
        }
    }

    private val prettyGson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

    init {
        title = "Generate Dart bean Class Code"
        setOKButtonText("Generate")
        okAction.apply {
            putValue(DEFAULT_ACTION, false)
            isEnabled = className.trim().isNotEmpty() && inputIsValidJson(json)
        }
        init()
    }

    override fun createCenterPanel() = panel {

        row {
            label("Please input the class name and JSON String for generating dart bean class")
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
                    myPreferredFocusedComponent = this
                    addKeyListener(SimpleKeyListener(onKeyPressed = { e ->
                        if (e.keyCode == KeyEvent.VK_TAB) {
                            e.consume()
                            if (e.isShiftDown) {
                                transferFocusBackward()
                                // Plan B
                                // KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent()
                                // Plan C
                                // val currentFocusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner
                                // val focusCycleRoot = KeyboardFocusManager.getCurrentKeyboardFocusManager().currentFocusCycleRoot
                                // val previousComponent = focusCycleRoot?.focusTraversalPolicy?.getComponentBefore(focusCycleRoot, currentFocusOwner)
                                // previousComponent?.requestFocusInWindow()
                            } else {
                                transferFocus()
                            }
                        }
                    }))
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

            val settings = ApplicationManager.getApplication().getService(Settings::class.java)
            checkBox(
                "ensureInitialized"
            ).bindSelected(settings.graph.ensureInitialized)

            checkBox(
                "constructor"
            ).bindSelected(settings.state::constructor)

            checkBox(
                "factory"
            ).bindSelected(
                settings.state::factory
            ).applyToComponent {
                this.toolTipText = "fromMap and fromJson factory"
            }

            checkBox(
                "nullable"
            ).bindSelected(
                settings.state::nullable
            )

            button("Settings") {

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
            throw Exception("className must not null or empty")
        }
        if (json.isEmpty()) {
            throw Exception("json must not null or empty")
        }

        if (doOkAction(className, json)) {
            super.doOKAction()
        }
    }

    class Graph(private val data: JsonInputDialog) {
        private val propertyGraph = PropertyGraph()

        val className = propertyGraph.propertyOf(data::className)
        val json = propertyGraph.propertyOf(data::json)

        fun afterPropagation(listener: Graph.() -> Unit) = apply {
            propertyGraph.afterPropagation { listener() }
        }
    }
}