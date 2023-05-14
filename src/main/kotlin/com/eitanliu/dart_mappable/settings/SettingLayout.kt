package com.eitanliu.dart_mappable.settings

import com.eitanliu.dart_mappable.extensions.graphProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.observable.util.bind
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign


class SettingLayout(private val settingState: Settings) {
    private val propertyGraph = PropertyGraph()
    var modelSuffix = settingState.modelSuffix

    private val modelSuffixProperty = propertyGraph.graphProperty(::modelSuffix)

    val rootPanel = panel {
        row {
            label("model suffix: ")
                .horizontalAlign(HorizontalAlign.LEFT)

            textField().applyToComponent {
                bind(modelSuffixProperty)
            }.horizontalAlign(HorizontalAlign.FILL)

            rowComment("Configure scan suffix files(Please separate them with commas)")
        }
        // separator()
    }

}
