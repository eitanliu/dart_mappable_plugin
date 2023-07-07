package com.eitanliu.dart.mappable.settings

import com.eitanliu.dart.mappable.utils.ApplicationUtils
import com.intellij.openapi.options.Configurable

class SettingConfig : Configurable {
    private val settings = ApplicationUtils.getSettings()

    private val layout = SettingLayout(settings)

    override fun getDisplayName(): String {
        return "DartMappable Settings"
    }

    override fun createComponent() = layout.rootPanel

    override fun isModified(): Boolean {
        return layout.isModified
    }

    override fun apply() {
        layout.apply()
    }

    override fun reset() {
        layout.reset()
    }

}
