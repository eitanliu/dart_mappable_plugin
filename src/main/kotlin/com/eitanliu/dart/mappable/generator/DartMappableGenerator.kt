package com.eitanliu.dart.mappable.generator

import com.eitanliu.dart.mappable.ast.*
import com.eitanliu.dart.mappable.extensions.*
import com.eitanliu.dart.mappable.generator.builder.JsonReflectableBuilder
import com.eitanliu.dart.mappable.settings.Settings
import com.eitanliu.dart.mappable.settings.SettingsOwner
import com.google.gson.*

class DartMappableGenerator(
    override val settings: Settings,
    className: String,
    json: String,
    dartFileName: DartFileName = DartFileName.Impl(className, settings.graph.modelSuffix.value),
) : DartGenerator.Self,
    JsonReflectableBuilder.Self,
    DartJsonParser,
    DartFileName by dartFileName,
    SettingsOwner {

    val fileMapperName = "$underscoreNameAndSuffix.mapper.dart"

    val jsonElement by lazy { JsonParser.parseString(json) }

    override fun generator() = CodeGenerator {
        val models = parserElement(jsonElement, camelCaseName)

        writeFileImports(models)
        writeFileParts(models)
        writeDataClasses(models)

    }

    private fun CodeGenerator.writeFileImports(models: List<DartClassModel>) {

        for (syntax in models.importsSyntax {
            importJsonReflectable()
            yield(DartImportModel("package:dart_mappable/dart_mappable.dart"))
        }) {
            writeln(syntax)
        }
    }

    private fun CodeGenerator.writeFileParts(models: List<DartClassModel>) {
        writeln()

        for (syntax in models.partsSyntax {
            yield(DartPartModel(fileMapperName, fileName))
        }) {
            writeln(syntax)
        }
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
            writeJsonReflectableClassAnnotation()
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
                    val syntax = member.buildSyntax(
                        camelCaseSuffix, final, nullable, !settings.constructor
                    )
                    writeln(syntax)
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
                    "this.${it.name.keyToFieldName()}"
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
                    fun guard(fn: String) = "_ensureContainer.$fn"

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

                    writeln()
                    writeScoped(
                        "static final MapperContainer _ensureContainer = () {", "}();"
                    ) {
                        writeln("$mapper.ensureInitialized();")
                        writeln("return MapperContainer.globals;")
                    }
                }

                // ensureInitialized
                writeln()
                writeln("static $mapper ensureInitialized() => $mapper.ensureInitialized();")
            }
        }
    }
}