package com.eitanliu.dart.mappable.models

class DartClassModel(
    val name: String,
    val imports: List<DartImportModel>,
    val members: List<DartMemberModel>,
    val functions: List<DartFunctionModel>,
)

class DartImportModel(
    val name: String,
    val alias: String? = null,
)

class DartMemberModel(
    val name: String,
    val type: String,
    val default: String? = null,
    val nullable: Boolean? = null,
    val isEntity: Boolean = false,
)

class DartFunctionModel(
    val name: String,
    val type: String,
    val params: List<DartMemberModel>
)