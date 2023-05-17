package com.eitanliu.dart.mappable.generator

import com.eitanliu.dart.mappable.extensions.camelCaseToUnderscore
import com.eitanliu.dart.mappable.extensions.toCamelCase
import com.eitanliu.dart.mappable.models.DartClassModel
import com.eitanliu.dart.mappable.models.DartFunctionModel
import com.eitanliu.dart.mappable.models.DartImportModel
import com.eitanliu.dart.mappable.models.DartMemberModel
import com.eitanliu.dart.mappable.settings.Settings
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.intellij.openapi.application.ApplicationManager
import java.math.BigDecimal
import java.math.BigInteger

class DartGenerator(
    private val className: String,
    private val json: String,
) {
    val underscoreName = className.camelCaseToUnderscore()

    val fileName = "$underscoreName.dart"

    val fileMappableName = "$underscoreName.mappable.dart"

    private val settings = ApplicationManager.getApplication().getService(Settings::class.java).state

    private val classSuffix = settings.modelSuffix.toCamelCase()

    private val jsonElement = JsonParser.parseString(json)

    private val generator = CodeGenerator {}

    fun generatorClassesString(): String {
        val models = parserElement(jsonElement, className)
        generator.writeFileImports(models)
        generator.writeDataClasses(models)
        return generator.builder.toString()
    }

    private fun CodeGenerator.writeFileImports(models: List<DartClassModel>) {
    }

    private fun CodeGenerator.writeDataClasses(models: List<DartClassModel>) {
    }

    fun parserElement(
        element: JsonElement,
        name: String,
        models: MutableList<DartClassModel> = mutableListOf(),
    ): MutableList<DartClassModel> {

        if (element is JsonArray) {
            if (!element.isEmpty) parserElement(element.first(), name, models)
        }

        if (element !is JsonObject) return models

        val sampleName = "$name$classSuffix"
        val imports = mutableListOf<DartImportModel>()
        val members = mutableListOf<DartMemberModel>()
        val functions = mutableListOf<DartFunctionModel>()

        val classModel = DartClassModel(sampleName, imports, members, functions)
        models.add(classModel)

        for (entry in element.entrySet()) {
            val key = entry.key
            val value = entry.value

            val memberModel = when {
                value.isJsonObject || value.isJsonArray -> {
                    val subName = "$name${key.toCamelCase()}"
                    parserElement(value, subName, models)
                    DartMemberModel(key, subName)
                }

                value is JsonPrimitive -> {
                    typeMapping(key, value)
                }

                else -> {
                    DartMemberModel(key, "dynamic")
                }
            }
            members.add(memberModel)


        }
        return models
    }

    // TODO: Type mapping
    private fun typeMapping(key: String, value: JsonPrimitive) = when {

        value.isString -> {
            DartMemberModel(key, "String")
        }

        value.isBoolean -> {
            DartMemberModel(key, "bool")
        }

        value.isNumber -> {
            when (value.asNumber) {
                is BigInteger, is Long, is Int, is Short, is Byte -> {
                    DartMemberModel(key, "int")
                }

                is BigDecimal, is Double, is Float -> {
                    DartMemberModel(key, "float")
                }

                else -> {
                    DartMemberModel(key, "num")
                }

            }
        }

        else -> {
            DartMemberModel(key, "dynamic")
        }
    }
}