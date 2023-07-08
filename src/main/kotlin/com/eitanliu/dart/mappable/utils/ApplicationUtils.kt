package com.eitanliu.dart.mappable.utils

import com.eitanliu.dart.mappable.settings.Settings
import com.intellij.openapi.application.ApplicationManager

object ApplicationUtils {
    fun getSettings(): Settings = ApplicationManager.getApplication().getService(Settings::class.java)
}