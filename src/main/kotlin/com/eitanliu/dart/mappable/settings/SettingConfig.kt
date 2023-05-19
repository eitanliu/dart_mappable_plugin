package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.extensions.value
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class SettingConfig : Configurable {
    private val settings = ApplicationManager.getApplication().getService(Settings::class.java).state

    private val layout = SettingLayout(settings)

    override fun isModified(): Boolean {
        return settings.graph.modelSuffix.value != layout.graph.modelSuffix.value
    }

    override fun getDisplayName(): String {
        return "DartMappableSettings"
    }

    override fun apply() {
        settings.graph.modelSuffix.value = layout.graph.modelSuffix.value
    }

    override fun reset() {
        layout.graph.modelSuffix.value = settings.graph.modelSuffix.value
    }

    override fun createComponent() = layout.rootPanel

}
