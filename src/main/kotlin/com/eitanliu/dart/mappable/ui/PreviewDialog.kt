@file:Suppress("DialogTitleCapitalization")

package com.eitanliu.dart.mappable.ui

import com.eitanliu.dart.mappable.ast.DartGenerator
import com.eitanliu.intellij.compat.binding.bindTabTransferFocus
import com.eitanliu.intellij.compat.dsl.LayoutAlign
import com.eitanliu.intellij.compat.dsl.layoutAlign
import com.eitanliu.intellij.compat.extensions.createPropertyGraph
import com.eitanliu.intellij.compat.extensions.propertyRef
import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

/**
 * Preview Dialog
 */
class PreviewDialog(
    private val project: Project,
    private val text: String = "",
) : DialogWrapper(
    project, true,
) {
    var generator: DartGenerator? = null
        private set

    val graph = Graph(this)

    init {
        title = "Preview Generate Code"
        setOKButtonText("Finish")
        init()
    }

    override fun createCenterPanel() = panel {

        row {
            resizableRow()
            // createEditor(DartLanguage.INSTANCE, project) {}
            textArea().apply {
                bindText(graph.text)
                applyToComponent {
                    rows = 10
                    myPreferredFocusedComponent = this
                    bindTabTransferFocus()
                }
                layoutAlign(LayoutAlign.FILL)
            }
        }

    }.apply {
        // preferredSize = JBDimension(600, 500)
        withPreferredSize(800, 600)
        withMinimumHeight(400)
    }

    class Graph(private val data: PreviewDialog) {
        private val propertyGraph: PropertyGraph = createPropertyGraph()

        val text = propertyGraph.propertyRef(data::text)

        fun afterPropagation(disposable: Disposable? = null, listener: Graph.() -> Unit) = apply {
            propertyGraph.afterPropagation(disposable) { listener() }
        }
    }
}