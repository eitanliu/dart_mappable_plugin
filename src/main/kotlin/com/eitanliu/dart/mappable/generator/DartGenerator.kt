package com.eitanliu.dart.mappable.generator

import com.eitanliu.dart.mappable.extensions.*
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
    className: String,
    json: String,
) {
    private val camelCaseName = className.keyToCamelCase(true)

    private val camelCaseSuffix = settings.graph.modelSuffix.value
        .replaceNonAlphabeticNumber("_")
        .underscoreToCamelCase(true)

    private val underscoreSuffix = camelCaseSuffix.camelCaseToUnderscore()

    private val underscoreNameAndSuffix = "${camelCaseName.camelCaseToUnderscore()}${
        if (underscoreSuffix.isEmpty()) "" else "_$underscoreSuffix"
    }"

    val fileName = "$underscoreNameAndSuffix.dart"

    val fileMapperName = "$underscoreNameAndSuffix.mapper.dart"

    val jsonElement by lazy { JsonParser.parseString(json) }

    fun generatorClassesString() = CodeGenerator {
        val models = parserElement(jsonElement, camelCaseName)

        writeFileImports(models)
        writeFileParts(models)
        writeDataClasses(models)

    }.builder.toString()

    private fun CodeGenerator.writeFileImports(models: List<DartClassModel>) {
        val imports = models.flatMapTo(mutableListOf()) { it.imports }
        imports.add(DartImportModel("package:dart_mappable/dart_mappable.dart"))
        imports.sortBy { it.name }

        for (import in imports) {
            val alias = import.alias?.let { " as $it" } ?: ""
            writeln("import '${import.name}'${alias};")
        }
    }

    private fun CodeGenerator.writeFileParts(models: List<DartClassModel>) {
        writeln()
        writeln("part '$fileMapperName';")
    }

    private fun CodeGenerator.writeDataClasses(models: List<DartClassModel>) {

        for (model in models) {

            val sampleName = "${model.name}$camelCaseSuffix"
            val mappable = "${sampleName}Mappable"
            val mapper = "${sampleName}Mapper"

            val enableMixin = settings.graph.enableMixin.value
            val enableFromJson = settings.graph.enableFromJson.value
            val enableToJson = settings.graph.enableToJson.value
            val enableFromMap = settings.graph.enableFromMap.value
            val enableToMap = settings.graph.enableToMap.value
            val enableCopyWith = settings.graph.enableCopyWith.value
            val fromMap = if (enableMixin) "fromMap" else settings.graph.mappableFromMap.value
            val toMap = if (enableMixin) "toMap" else settings.graph.mappableToMap.value
            val fromJson = if (enableMixin) "fromJson" else settings.graph.mappableFromJson.value
            val toJson = if (enableMixin) "toJson" else settings.graph.mappableToJson.value

            writeln()
            writeln("@MappableClass()")
            writeScoped(buildString {
                append("class $sampleName")
                if (enableMixin) append(" with $mappable")
                append(" {")
            }, "}") {
                for (member in model.members) {

                    if (member.name.needAnnotation()) {
                        writeln("@MappableField(key: '${member.name}')")
                    }

                    val nullable = member.nullable ?: settings.nullable
                    val final = settings.final
                    writeln(buildString {
                        if (final) append("final ")
                        // type
                        fun addType() {
                            append(member.type)
                            if (member.entity) append(camelCaseSuffix)
                            if (nullable && member.type != "dynamic") append("?")
                        }
                        if (member.collection) {
                            append("List<")
                            addType()
                            append(">")
                            if (nullable) append("?")
                        } else {
                            addType()
                        }

                        // name
                        append(" ${member.name.keyToCamelCase()}")
                        // default
                        if (!settings.constructor) {
                            if (!nullable) {
                                append(" = ${typeDefault(member)}")
                            }
                        }
                        append(";")
                    })
                }

                // constructor
                writeln()
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
                writeln("$sampleName($params);")
                // writeScoped("$sampleName($params) {", "}") {
                //     writeln("$mapper.ensureInitialized();")
                // }

                if (enableMixin) {
                    // factory fromMap
                    writeln()
                    writeln("factory $sampleName.$fromMap(Map<String, dynamic> map) => $mapper.fromMap(map);")

                    // factory fromJson
                    writeln()
                    writeln("factory $sampleName.$fromJson(String json) => $mapper.fromJson(json);")
                } else {
                    fun guard(fn: String) = "$mapper._guard((c) => c.$fn)"

                    // factory fromMap
                    if (enableFromMap) {
                        writeln()
                        writeln(
                            "factory $sampleName.$fromMap(Map<String, dynamic> map) => ${guard("fromMap<$sampleName>(map)")};"
                        )
                    }

                    // factory fromJson
                    if (enableFromJson) {
                        writeln()
                        writeln("factory $sampleName.$fromJson(String json) => ${guard("fromJson<$sampleName>(json)")};")
                    }

                    // toMap
                    if (enableToMap) {
                        writeln()
                        writeScoped("Map<String, dynamic> $toMap() {", "}") {
                            writeln("return ${guard("toMap(this)")};")
                        }
                    }

                    // toJson
                    if (enableToJson) {
                        writeln()
                        if (toJson == "toString") writeln("@override")
                        writeScoped("String $toJson() {", "}") {
                            writeln("return ${guard("toJson(this)")};")
                        }
                    }

                    // toString
                    if (!enableToJson || toJson != "toString") {
                        writeln()
                        writeln("@override")
                        writeScoped("String toString() {", "}") {
                            writeln("return ${guard("asString(this)")};")
                        }
                    }

                    // equal
                    writeln()
                    writeln("@override")
                    writeScoped("bool operator ==(Object other) {", "}") {
                        writeln(
                            "return identical(this, other) || " +
                                    "(runtimeType == other.runtimeType && ${guard("isEqual(this, other)")});"
                        )
                    }

                    // hashCode
                    writeln()
                    writeln("@override")
                    writeScoped("int get hashCode {", "}") {
                        writeln("return ${guard("hash(this)")};")
                    }

                    // copyWith
                    if (enableCopyWith) {
                        writeln()
                        writeScoped(
                            "${sampleName}CopyWith<$sampleName, $sampleName, $sampleName> get copyWith {",
                            "}"
                        ) {
                            writeln("return _${sampleName}CopyWithImpl(this, \$identity, \$identity);")
                        }
                    }
                }

                // ensureInitialized
                writeln()
                writeln("static $mapper ensureInitialized() => $mapper.ensureInitialized();")
            }
        }
    }

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

    private fun typeDefault(member: DartMemberModel) = when {
        member.nullable ?: settings.nullable -> "null"
        member.collection -> "List.empty(growable: true)"
        member.entity -> "${member.type}$camelCaseSuffix()"
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
        return replaceNonAlphabeticNumber("_")
            .underscoreToCamelCase(capitalizeFirstWord)
            .numberAddPrefix()
    }

    private fun String.needAnnotation(): Boolean {
        val regex = Regex("^[a-z][a-zA-Z0-9]*$")
        return !matches(regex)
    }

    private fun String.numberAddPrefix(prefix: String = "$"): String {
        val regex = Regex("^\\d.+$")
        return if (matches(regex)) "$prefix$this" else this
    }
}