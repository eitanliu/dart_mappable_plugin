package com.eitanliu.dart_mappable.settings

import com.eitanliu.dart_mappable.extensions.value
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class SettingConfig : Configurable {
    private var settingLayout: SettingLayout? = null

    override fun isModified(): Boolean {
        return settingLayout?.run {
            // getSettings() != Settings(
            //     modelSuffix, false, false
            // )
            getSettings().modelSuffix != graph.modelSuffix.value
        } ?: false
    }

    override fun getDisplayName(): String {
        return "DartMappableSettings"
    }

    override fun apply() {
        settingLayout?.also { layout ->
            getSettings().modelSuffix = layout.graph.modelSuffix.value
        }
    }

    override fun reset() {
        settingLayout?.also { layout ->
            layout.graph.modelSuffix.value = getSettings().modelSuffix
        }
    }


    override fun createComponent(): JComponent {
        return SettingLayout(getSettings()).apply {
            settingLayout = this
        }.rootPanel
    }

    private fun getSettings(): Settings {
        return ApplicationManager.getApplication().getService(Settings::class.java).state
    }

}
