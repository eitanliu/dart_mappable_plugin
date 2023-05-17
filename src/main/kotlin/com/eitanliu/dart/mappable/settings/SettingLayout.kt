package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.extensions.propertyOf
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign


class SettingLayout(private val settingState: Settings) {
    val graph = Graph(this)

    var modelSuffix = settingState.modelSuffix

    val rootPanel = panel {
        row {
            label("Model suffix: ")
                .horizontalAlign(HorizontalAlign.LEFT)

            textField().apply {
                bindText(graph.modelSuffix)
            }.horizontalAlign(HorizontalAlign.FILL)

            rowComment("Configure scan suffix files(Please separate them with commas)")
        }
        // separator()
    }

    class Graph(private val data: SettingLayout) {
        private val propertyGraph = PropertyGraph()

        val modelSuffix = propertyGraph.propertyOf(data::modelSuffix)
    }
}
