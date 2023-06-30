package com.eitanliu.dart.mappable.ast

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.math.BigDecimal
import java.math.BigInteger

interface DartJsonParser : DartFileName.Extensions {

    /**
     * parser Abstract Syntax Tree；AST
     */
    fun parserElement(
        element: JsonElement,
        name: String,
        models: MutableList<DartClassModel> = mutableListOf(),
    ): MutableList<DartClassModel> {

        if (element is JsonArray) {
            if (element.size() != 0) parserElement(element.first(), name, models)
        }

        if (element !is JsonObject) return models

        val imports = mutableListOf<DartImportModel>()
        val members = mutableListOf<DartMemberModel>()
        val functions = mutableListOf<DartFunctionModel>()

        val classModel = DartClassModel(name, imports, members, functions)
        models.add(classModel)

        for ((key, value) in element.entrySet()) {

            val memberModel = when {
                value.isJsonObject -> {
                    val subName = "$name${key.keyToCamelCase(true)}"
                    parserElement(value, subName, models)
                    DartMemberModel(key, subName, entity = true)
                }

                value.isJsonArray -> {
                    val item = value.asJsonArray.firstOrNull()
                    when {
                        item == null -> DartMemberModel(key, "dynamic", collection = true)
                        item is JsonPrimitive -> typeMapping(key, item).copy(collection = true)
                        else -> {
                            val subName = "$name${key.keyToCamelCase(true)}"
                            parserElement(value, subName, models)
                            DartMemberModel(key, subName, collection = true, entity = true)
                        }
                    }
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

    private fun typeMapping(key: String, value: JsonPrimitive) = when {

        value.isString -> {
            DartMemberModel(key, "String")
        }

        value.isBoolean -> {
            DartMemberModel(key, "bool")
        }

        value.isNumber -> {
            when (val num = value.asNumber) {
                is BigInteger, is Long, is Int, is Short, is Byte -> {
                    DartMemberModel(key, "int")
                }

                is BigDecimal, is Double, is Float -> {
                    DartMemberModel(key, "double")
                }

                else -> {
                    DartMemberModel(key, num.getType())
                }

            }
        }

        else -> {
            DartMemberModel(key, "dynamic")
        }
    }

    private fun Number.getType(): String {
        val numberString = toString()

        return when {
            numberString.matches(Regex("^[+-]?\\d+$")) -> "int"
            numberString.matches(Regex("^[+-]?\\d+\\.\\d+$")) -> "double"
            else -> "num"
        }
    }
}