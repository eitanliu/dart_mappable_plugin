@file:Suppress("NAME_SHADOWING")

package com.eitanliu.dart.mappable.ast

interface DartGenerator : DartFileName {

    fun buildString() = generator().builder.toString()

    fun generator(): CodeGenerator

    interface Self : DartGenerator, Ext

    interface Ext : DartFileName.Ext {

        fun Iterable<DartMemberModel>.paramsSyntax(
            entitySuffix: String,
            nullable: Boolean = false,
            member: Boolean = false,
            namedFunction: Boolean = false,
            beforeScope: (suspend SequenceScope<DartMemberModel>.() -> Unit)? = null,
            afterScope: (suspend SequenceScope<DartMemberModel>.() -> Unit)? = null,
            beforeTransform: (StringBuilder.(DartMemberModel) -> Unit)? = null,
            afterTransform: (StringBuilder.(DartMemberModel) -> Unit)? = null,
        ): Sequence<String> {
            val members = sequence {
                if (beforeScope != null) beforeScope()
                yieldAll(asSequence())
                if (afterScope != null) afterScope()
            }

            return members
                .distinctBy { it.name }
                .map {
                    val name = it.name.keyToFieldName()
                    if (member) return@map "this.$name"

                    val nullable = it.nullable ?: nullable
                    val type = it.typeName(entitySuffix, nullable)
                    buildString {
                        beforeTransform?.invoke(this, it)
                        if (namedFunction && !nullable) {
                            append("required ")
                        }
                        append(type)
                        append(" ")
                        append(name)
                        afterTransform?.invoke(this, it)
                    }
                }
        }

        fun Iterable<DartClassModel>.importsSyntax(block: suspend SequenceScope<DartImportModel>.() -> Unit): Sequence<String> {
            val imports = sequence {
                block()
                yieldAll(asSequence().flatMap { it.imports })
            }

            return imports
                .map { it.buildSyntax() }
                .distinct()
                .sorted()
        }

        fun Iterable<DartClassModel>.partsSyntax(block: suspend SequenceScope<DartPartModel>.() -> Unit): Sequence<String> {
            val parts = sequence {
                block()
                yieldAll(asSequence().flatMap { it.parts })
            }

            return parts
                .map { it.buildSyntax() }
                .distinct()
                .sorted()
        }

        fun DartImportModel.buildSyntax() = buildString {
            append("import '$name'")
            if (alias != null && alias.trim().isNotEmpty()) {
                append(" as $alias")
            } else if (visibility != null) {
                append("${visibility.visible} ${visibility.list.joinToString()}")
            }
            append(";")
        }

        fun DartPartModel.buildSyntax() = buildString {
            if (slice) {
                append("part of '$of'")
            } else {
                append("part '$part'")
            }
            append(";")
        }

        fun DartMemberModel.buildSyntax(
            entitySuffix: String,
            final: Boolean = false,
            nullable: Boolean = false,
            default: Boolean = false,
            buildDefault: (member: DartMemberModel, entitySuffix: String, nullable: Boolean) -> String = ::typeDefault,
        ) = buildString {
            val member = this@buildSyntax;
            if (final) append("final ")
            val nullable = member.nullable ?: nullable
            // type
            append(typeName(entitySuffix, nullable))

            // name
            append(" ${member.name.keyToFieldName()}")
            // default
            if (default && !nullable) {
                append(" = ${buildDefault(member, entitySuffix, false)}")
            }
            append(";")
        }

        fun DartMemberModel.typeName(
            entitySuffix: String,
            nullable: Boolean = false,
        ) = buildString {
            val nullable = this@typeName.nullable ?: nullable
            fun name() {
                append(type)
                if (entity) append(entitySuffix)
                if (nullable && type != "dynamic") append("?")
            }
            if (collection) {
                append("List<")
                name()
                append(">")
                if (nullable) append("?")
            } else {
                name()
            }
        }

        fun typeDefault(
            member: DartMemberModel,
            entitySuffix: String,
            nullable: Boolean = false
        ) = when {
            member.nullable ?: nullable -> "null"
            member.collection -> "List.empty(growable: true)"
            member.entity -> "${member.type}$entitySuffix()"
            else -> when (member.type) {
                "String" -> "''"
                "bool" -> "false"
                "int" -> "0"
                "double" -> "0.0"
                "dynamic" -> "null"
                else -> "$${member.type}()"
            }
        }

        fun String.needAnnotation(): Boolean {
            val regex = Regex("^[a-z][a-zA-Z0-9]*$")
            return !matches(regex)
        }
    }
}

