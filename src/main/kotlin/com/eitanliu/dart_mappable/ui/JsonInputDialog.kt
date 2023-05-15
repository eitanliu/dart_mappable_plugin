@file:Suppress("DialogTitleCapitalization")

package com.eitanliu.dart_mappable.ui

import com.eitanliu.dart_mappable.settings.Settings
import com.google.gson.*
import com.intellij.codeInspection.javaDoc.JavadocUIUtil.bindCheckbox
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.messages.MessageDialog
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import com.intellij.util.ui.JBDimension
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JComponent
import javax.swing.JTextField
import javax.swing.text.JTextComponent

class JsonInputValidator : InputValidator {

    lateinit var classNameField: JTextField
    override fun checkInput(inputString: String): Boolean {
        return try {
            val classNameLegal = classNameField.text.trim().isNotBlank()
            inputIsValidJson(inputString) && classNameLegal
        } catch (e: JsonSyntaxException) {
            false
        }

    }

    private fun inputIsValidJson(string: String) = try {
        val jsonElement = JsonParser.parseString(string)
        (jsonElement.isJsonObject || jsonElement.isJsonArray)
    } catch (e: JsonSyntaxException) {
        false
    }

    override fun canClose(inputString: String): Boolean {
        return true
    }
}

val jsonInputValidator = JsonInputValidator()

/**
 * Json input Dialog
 */
open class JsonInputDialog(
    project: Project,
    val inputModelBlock: (className: String, json: String) -> Boolean
) : MessageDialog(
    project,
    "Please input the class name and JSON String for generating dart bean class",
    "Generate Dart bean Class Code",
    arrayOf(Messages.getCancelButton(), Messages.getOkButton()),
    1, null, true,
) {
    var text = ""

    private lateinit var classNameInput: JTextField

    private val prettyGson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

    init {
        setOKButtonText("Generate")
    }

    override fun createCenterPanel() = panel {

        myMessage?.let { row(it) {} }
        row { label("Class Name:") }
        row {
            textField().horizontalAlign(HorizontalAlign.FILL)
        }
        row {
            label("JSON Text:")
            button("Format") {
            }.horizontalAlign(HorizontalAlign.RIGHT)
        }
        row {
            resizableRow()
            textArea().apply {
                applyToComponent {
                    addKeyListener(object : KeyListener {
                        override fun keyTyped(e: KeyEvent) {
                        }

                        override fun keyPressed(e: KeyEvent) {
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
                        }

                        override fun keyReleased(e: KeyEvent) {
                        }
                    })
                }
                horizontalAlign(HorizontalAlign.FILL)
                verticalAlign(VerticalAlign.FILL)
            }
        }
        row {

            checkBox(
                "ensureInitialized"
            ).bindCheckbox(
                ApplicationManager.getApplication().getService(Settings::class.java).state::ensureInitialized
            )

            checkBox(
                "constructor"
            ).bindCheckbox(
                ApplicationManager.getApplication().getService(Settings::class.java).state::constructor
            )

            checkBox(
                "factory"
            ).bindCheckbox(
                ApplicationManager.getApplication().getService(Settings::class.java).state::factory
            ).applyToComponent {
                this.toolTipText = "fromMap and fromJson factory"
            }

            checkBox(
                "nullable"
            ).bindCheckbox(
                ApplicationManager.getApplication().getService(Settings::class.java).state::nullable
            )

            button("Settings") {

            }.horizontalAlign(HorizontalAlign.RIGHT)
        }

    }.apply {
        preferredSize = JBDimension(600, 500)
    }


    override fun getPreferredFocusedComponent(): JComponent? {
        // return if (classNameInput.text?.isEmpty() == false) {
        //     myField
        // } else {
        //     classNameInput
        // }
        return super.getPreferredFocusedComponent()
    }

    fun handleFormatJSONString(textField: JTextComponent) {
        val currentText = textField.text ?: ""
        if (currentText.isNotEmpty()) {
            try {
                val jsonElement = prettyGson.fromJson<JsonElement>(currentText, JsonElement::class.java)
                val formatJSON = prettyGson.toJson(jsonElement)
                textField.text = formatJSON
            } catch (e: Exception) {
            }
        }

    }

    override fun doOKAction() {
        val className = classNameInput.text
        val json = "myField.text"

        if (className.isEmpty()) {
            throw Exception("className must not null or empty")
        }
        if (json.isEmpty()) {
            throw Exception("json must not null or empty")
        }

        if (inputModelBlock(className, json)) {
            super.doOKAction()
        }
    }
}