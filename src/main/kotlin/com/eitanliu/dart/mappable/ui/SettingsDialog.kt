package com.eitanliu.dart.mappable.ui

import com.eitanliu.dart.mappable.settings.SettingLayout
import com.eitanliu.dart.mappable.settings.Settings
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.JBDimension
import java.awt.Component

class SettingsDialog(
    project: Project? = null,
    parent: Component? = null,
    canBeParent: Boolean = false,
) : DialogWrapper(
    project, parent, canBeParent, IdeModalityType.IDE
) {
    private val settings = ApplicationManager.getApplication().getService(Settings::class.java).state

    private val layout = SettingLayout(settings)

    init {
        title = "DartMappable Settings"
        init()
    }

    override fun createCenterPanel() = layout.createComponent().apply {

        // preferredSize = JBDimension(600, 500)
    }

    override fun doOKAction() {
        layout.apply()
        super.doOKAction()
    }
}