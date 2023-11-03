package com.eitanliu.dart.mappable.generator

import com.eitanliu.dart.mappable.ast.DartGenerator
import com.eitanliu.intellij.compat.extensions.value
import com.eitanliu.dart.mappable.settings.Implements
import com.eitanliu.dart.mappable.settings.Settings

fun buildDartGenerator(
    settings: Settings,
    className: String,
    json: String,
): DartGenerator = when (settings.graph.implement.value) {
    Implements.JSON_SERIALIZABLE -> JsonSerializableGenerator(settings, className, json)
    Implements.DART_MAPPABLE -> DartMappableGenerator(settings, className, json)
    Implements.FREEZED -> FreezedGenerator(settings, className, json)
    else -> JsonSerializableGenerator(settings, className, json)
}