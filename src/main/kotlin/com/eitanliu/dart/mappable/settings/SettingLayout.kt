package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.extensions.propertyOf
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.ui.layout.panel


class SettingLayout(private val setting: Settings) {
    val graph = Graph(this)

    val rootPanel = panel {
        row {
            label("Model suffix:")

            textField(graph.modelSuffix).apply {
                constraints(pushX)
            }
        }
        row { label("Configure dart bean model files suffix.") }
        // row(null as String?, true) {}
    }

    class Graph(data: SettingLayout) {
        private val propertyGraph = PropertyGraph()

        val modelSuffix = propertyGraph.propertyOf(data.setting.modelSuffix)
    }
}
