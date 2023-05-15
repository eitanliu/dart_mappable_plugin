package com.eitanliu.dart_mappable.settings

import com.eitanliu.dart_mappable.extensions.value
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class SettingConfig : Configurable {
    private var settingLayout: SettingLayout? = null

    private val settings = ApplicationManager.getApplication().getService(Settings::class.java).state

    override fun isModified(): Boolean {
        return settingLayout?.run {
            // settings != Settings(
            //     modelSuffix, false, false
            // )
            settings.graph.modelSuffix.value != graph.modelSuffix.value
        } ?: false
    }

    override fun getDisplayName(): String {
        return "DartMappableSettings"
    }

    override fun apply() {
        settingLayout?.also { layout ->
            settings.graph.modelSuffix.value = layout.graph.modelSuffix.value
        }
    }

    override fun reset() {
        settingLayout?.also { layout ->
            layout.graph.modelSuffix.value = settings.graph.modelSuffix.value
        }
    }


    override fun createComponent(): JComponent {
        return SettingLayout(settings).apply {
            settingLayout = this
        }.rootPanel
    }

}
