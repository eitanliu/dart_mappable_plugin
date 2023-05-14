package com.eitanliu.dart_mappable.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class SettingComponent : Configurable {
    private var settingLayout: SettingLayout? = null
    override fun isModified(): Boolean {
        return settingLayout?.run {
            // getSettings() != Settings(
            //     modelSuffix, false, false
            // )
            getSettings().modelSuffix != modelSuffix
        } ?: false
    }

    override fun getDisplayName(): String {
        return "DartMappableSettings"
    }

    override fun apply() {
        settingLayout?.let { layout ->
            getSettings().modelSuffix = layout.modelSuffix
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
