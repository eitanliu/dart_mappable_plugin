package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.extensions.propertyOf
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign


class SettingLayout(private val setting: Settings) {
    val graph = Graph(this)

    val rootPanel = panel {
        row {
            label("Model suffix:").horizontalAlign(HorizontalAlign.LEFT)

            textField().apply {
                bindText(graph.modelSuffix)
            }.horizontalAlign(HorizontalAlign.FILL)

            rowComment("Configure dart bean model files suffix.")
        }
        // separator()
    }

    class Graph(data: SettingLayout) {
        private val propertyGraph = PropertyGraph()

        val modelSuffix = propertyGraph.propertyOf(data.setting.modelSuffix)
    }
}
