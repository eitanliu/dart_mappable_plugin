package com.eitanliu.dart.mappable.generator

import com.eitanliu.dart.mappable.extensions.camelCaseToUnderscore
import com.eitanliu.dart.mappable.extensions.replaceNonAlphabetic
import com.eitanliu.dart.mappable.extensions.toCamelCase
import com.eitanliu.dart.mappable.extensions.underscoreToCamelCase
import com.eitanliu.dart.mappable.models.DartClassModel
import com.eitanliu.dart.mappable.models.DartFunctionModel
import com.eitanliu.dart.mappable.models.DartImportModel
import com.eitanliu.dart.mappable.models.DartMemberModel
import com.eitanliu.dart.mappable.settings.Settings
import com.google.gson.*
import java.math.BigDecimal
import java.math.BigInteger

class DartGenerator(
    private val settings: Settings,
    private val className: String,
    private val json: String,
) {
    val modelSuffix = settings.modelSuffix.camelCaseToUnderscore()

    val underscoreName = "${className.camelCaseToUnderscore()}${
        if (modelSuffix.isEmpty()) "" else "_$modelSuffix"
    }"

    val fileName = "$underscoreName.dart"

    val fileMapperName = "$underscoreName.mapper.dart"

    private val classSuffix = settings.modelSuffix.toCamelCase(true)

    private val jsonElement = JsonParser.parseString(json)

    private val generator = CodeGenerator {}

    fun generatorClassesString(): String {
        val models = parserElement(jsonElement, className)
        generator.writeFileImports(models)
        generator.writeFileParts(models)
        generator.writeDataClasses(models)
        return generator.builder.toString()
    }

    private fun CodeGenerator.writeFileImports(models: List<DartClassModel>) {
        val imports = models.flatMapTo(mutableListOf()) { it.imports }
        imports.add(DartImportModel("package:dart_mappable/dart_mappable.dart"))
        imports.sortBy { it.name }

        for (import in imports) {
            val alias = import.alias?.let { " as $it" } ?: ""
            writeln("import '${import.name}'${alias};")
        }
        writeln()
    }


    private fun CodeGenerator.writeFileParts(models: List<DartClassModel>) {
        writeln("part '$fileMapperName';")
        writeln()
    }

    private fun CodeGenerator.writeDataClasses(models: List<DartClassModel>) {

        val last = models.last()
        for (model in models) {

            val sampleName = "${model.name}$classSuffix"
            val mappable = "${sampleName}Mappable"
            val mapper = "${sampleName}Mapper"

            writeln("@MappableClass()")
            writeScoped("class $sampleName with $mappable {", "}") {
                for (member in model.members) {

                    if (member.name.needAnnotation()) {
                        writeln("@MappableField(key: '${member.name}')")
                    }

                    val nullable = member.nullable ?: settings.nullable
                    writeln(buildString {
                        // type
                        append(member.type)
                        if (member.isEntity) append(classSuffix)
                        if (nullable && member.name != "dynamic") append("?")
                        // name
                        append(" ${member.name.keyToCamelCase()}")
                        // default
                        if (!settings.constructor) append(" = ${typeDefault(member)}")
                        append(";")
                    })
                }
                writeln()

                // constructor
                val params = if (settings.constructor) model.members.joinToString(
                    separator = ", ", prefix = "", postfix = ""
                ) {
                    // val nullable = it.nullable ?: settings.nullable
                    // buildString {
                    //     // if (!nullable) this.append("required ")
                    //     append("this.${it.name.keyToCamelCase()}")
                    //     if (!nullable) append(" = ${typeDefault(it.type)}")
                    // }
                    "this.${it.name.keyToCamelCase()}"
                } else ""
                writeScoped("$sampleName($params) {", "}") {
                    writeln("$mapper.ensureInitialized();")
                }
                writeln()

                // factory
                writeln("factory $sampleName.fromMap(Map<String, dynamic> map) => $mapper.fromMap(map);")
                writeln()

                writeln("factory $sampleName.fromJson(String json) => $mapper.fromJson(json);")
                writeln()

                // ensureInitialized
                writeln("static $mapper ensureInitialized() => $mapper.ensureInitialized();")
            }
            if (model != last) writeln()
        }
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

        val imports = mutableListOf<DartImportModel>()
        val members = mutableListOf<DartMemberModel>()
        val functions = mutableListOf<DartFunctionModel>()

        val classModel = DartClassModel(name, imports, members, functions)
        models.add(classModel)

        for ((key, value) in element.entrySet()) {

            val memberModel = when {
                value.isJsonObject || value.isJsonArray -> {
                    val subName = "$name${key.keyToCamelCase(true)}"
                    parserElement(value, subName, models)
                    DartMemberModel(key, subName, isEntity = true)
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

    private fun typeDefault(member: DartMemberModel) = when {
        member.nullable ?: settings.nullable -> "null"
        member.isEntity -> "${member.type}$classSuffix()"
        else -> when (member.type) {
            "String" -> "''"
            "bool" -> "false"
            "int" -> "0"
            "double" -> "0.0"
            "dynamic" -> "null"
            else -> "$${member.type}()"
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

    private fun String.keyToCamelCase(capitalizeFirstWord: Boolean = false): String {
        return replaceNonAlphabetic("_").underscoreToCamelCase(capitalizeFirstWord)
    }

    private fun String.needAnnotation(): Boolean {
        val regex = Regex("^[a-z][a-z0-9_]*$")
        return !matches(regex)
    }
}