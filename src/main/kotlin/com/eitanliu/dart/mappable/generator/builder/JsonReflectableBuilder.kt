package com.eitanliu.dart.mappable.generator.builder

import com.eitanliu.dart.mappable.ast.CodeGenerator
import com.eitanliu.dart.mappable.ast.DartImportModel
import com.eitanliu.intellij.compat.extensions.value
import com.eitanliu.dart.mappable.settings.SettingsOwner

interface JsonReflectableBuilder : SettingsOwner {

    val enableJsonReflectable: Boolean

    interface Self : JsonReflectableBuilder {

        override val enableJsonReflectable get() = settings.graph.enableJsonReflectable.value

        suspend fun SequenceScope<DartImportModel>.importJsonReflectable() {
            if (enableJsonReflectable)
                yield(DartImportModel("package:json_reflectable/json_reflectable.dart"))
        }

        fun CodeGenerator.writeJsonReflectableClassAnnotation() {
            if (enableJsonReflectable) writeln("@jsonReflector")
        }
    }
}