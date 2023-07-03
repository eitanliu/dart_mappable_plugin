package com.eitanliu.dart.mappable.generator

import com.eitanliu.dart.mappable.ast.*
import com.eitanliu.dart.mappable.extensions.*
import com.eitanliu.dart.mappable.settings.Settings
import com.google.gson.*

class JsonSerializableGenerator(
    private val settings: Settings,
    className: String,
    json: String,
    dartFileName: DartFileName = DartFileName.Default(className, settings.graph.modelSuffix.value),
) : DartGenerator,
    DartGenerator.Extensions,
    DartJsonParser,
    DartFileName by dartFileName {

    val fileGeneratorName = "$underscoreNameAndSuffix.g.dart"

    val jsonElement by lazy { JsonParser.parseString(json) }

    override fun generator() = CodeGenerator {
        val models = parserElement(jsonElement, camelCaseName)

        writeFileImports(models)
        writeFileParts(models)
        writeDataClasses(models)

    }

    private fun CodeGenerator.writeFileImports(models: List<DartClassModel>) {

        for (syntax in models.importsSyntax {
            yield(DartImportModel("package:json_annotation/json_annotation.dart"))
        }) {
            writeln(syntax)
        }
    }

    private fun CodeGenerator.writeFileParts(models: List<DartClassModel>) {
        writeln()

        for (syntax in models.partsSyntax {
            yield(DartPartModel(fileGeneratorName, fileName))
        }) {
            writeln(syntax)
        }
    }

    private fun CodeGenerator.writeDataClasses(models: List<DartClassModel>) {

        for (model in models) {

            val sampleName = "${model.name}$camelCaseSuffix"

            val constructor = settings.graph.constructor.value

            writeln()
            writeln("@JsonSerializable()")
            writeScoped(buildString {
                append("class $sampleName")
                append(" {")
            }, "}") {
                for (member in model.members) {

                    if (member.name.needAnnotation()) {
                        writeln("@JsonKey(name: '${member.name}')")
                    }

                    val nullable = member.nullable ?: settings.nullable
                    val final = settings.final
                    val syntax = member.buildSyntax(
                        camelCaseSuffix, final, nullable, !constructor
                    )
                    writeln(syntax)
                }

                // constructor
                writeln()
                val params = if (constructor) model.members.joinToString(
                    separator = ", ", prefix = "", postfix = ""
                ) {
                    "this.${it.name.keyToCamelCase()}"
                } else ""
                writeln("$sampleName($params);")

                // factory fromJson
                writeln()
                writeln("factory $sampleName.fromJson(Map<String, dynamic> json) => _\$${sampleName}FromJson(json);")

                // toJson
                writeln()
                writeln("Map<String, dynamic> toJson() => _\$${sampleName}ToJson(this);")
            }
        }
    }
}