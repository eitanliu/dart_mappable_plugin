package com.eitanliu.dart.mappable.generator

import com.eitanliu.dart.mappable.ast.*
import com.eitanliu.dart.mappable.extensions.*
import com.eitanliu.dart.mappable.generator.builder.JsonReflectableBuilder
import com.eitanliu.dart.mappable.settings.Settings
import com.eitanliu.dart.mappable.settings.SettingsOwner
import com.google.gson.*

class FreezedGenerator(
    override val settings: Settings,
    className: String,
    json: String,
    dartFileName: DartFileName = DartFileName.Impl(className, settings.graph.modelSuffix.value),
) : DartGenerator.Self,
    JsonReflectableBuilder.Self,
    DartJsonParser,
    DartFileName by dartFileName,
    SettingsOwner {

    val enableJson by lazy { settings.graph.freezedEnableJson.value }

    val fileFreezedName = "$underscoreNameAndSuffix.freezed.dart"

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
            importJsonReflectable()
            yield(DartImportModel("package:freezed_annotation/freezed_annotation.dart"))
        }) {
            writeln(syntax)
        }
    }

    private fun CodeGenerator.writeFileParts(models: List<DartClassModel>) {
        writeln()

        for (syntax in models.partsSyntax {
            yield(DartPartModel(fileFreezedName, fileName))
            if (enableJson) {
                yield(DartPartModel(fileGeneratorName, fileName))
            }
        }) {
            writeln(syntax)
        }
    }

    private fun CodeGenerator.writeDataClasses(models: List<DartClassModel>) {

        for (model in models) {

            val sampleName = "${model.name}$camelCaseSuffix"
            val mixinName = "_\$${sampleName}"

            val nullable = settings.graph.nullable.value
            val final = settings.graph.final.value

            writeln()
            writeJsonReflectableClassAnnotation()
            writeln(if (final) "@freezed" else "@unfreezed")
            writeScoped(buildString {
                append("class $sampleName")
                append(" with $mixinName")
                append(" {")
            }, "}") {

                // factory constructor
                writeln()
                writeln(buildString {
                    if (final) append("const ")
                    append("factory ")
                    append("$sampleName(")
                    if (model.members.isNotEmpty()) {
                        val params = model.members.joinToString(
                            separator = ", ", prefix = "", postfix = ","
                        ) { member ->
                            member.buildParam(nullable)
                        }
                        append("{$params}")
                    }
                    append(") = _$sampleName;")
                })

                if (enableJson) {
                    // factory fromJson
                    writeln()
                    writeln("factory $sampleName.fromJson(Map<String, dynamic> json) => _\$${sampleName}FromJson(json);")

                    // toJson
                    // writeln()
                    // writeln("Map<String, dynamic> toJson() => _\$${sampleName}ToJson(this);")
                }
            }
        }
    }

    private fun DartMemberModel.buildParam(nullable: Boolean) = buildString {
        val member = this@buildParam
        val mNullable = member.nullable ?: nullable
        if (member.name.needAnnotation()) {
            append("@JsonKey(name: '${member.name}') ")
        }
        if (!mNullable) append("required ")
        append(member.typeName(camelCaseSuffix, mNullable))
        append(" ")
        append(member.name.keyToFieldName())
    }
}