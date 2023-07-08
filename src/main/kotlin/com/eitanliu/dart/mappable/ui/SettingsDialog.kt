package com.eitanliu.dart.mappable.ui

import com.eitanliu.dart.mappable.settings.SettingLayout
import com.eitanliu.dart.mappable.utils.ApplicationUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.Component

class SettingsDialog(
    project: Project? = null,
    parent: Component? = null,
    canBeParent: Boolean = false,
) : DialogWrapper(
    project, parent, canBeParent, IdeModalityType.IDE
) {
    private val settings = ApplicationUtils.getSettings()

    private val layout = SettingLayout(settings)

    init {
        title = "DartMappable Settings"
        init()
    }

    override fun createCenterPanel() = layout.rootPanel.apply {

        // preferredSize = JBDimension(600, 500)
    }

    override fun doOKAction() {
        super.doOKAction()
    }
}