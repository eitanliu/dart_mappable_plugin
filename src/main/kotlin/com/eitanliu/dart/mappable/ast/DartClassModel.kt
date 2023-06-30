package com.eitanliu.dart.mappable.ast

data class DartClassModel(
    val name: String,
    val imports: List<DartImportModel>,
    val members: List<DartMemberModel>,
    val functions: List<DartFunctionModel>,
)

data class DartImportModel(
    val name: String,
    val alias: String? = null,
)

data class DartMemberModel(
    val name: String,
    val type: String,
    val default: String? = null,
    val nullable: Boolean? = null,
    val collection: Boolean = false,
    val entity: Boolean = false,
)

data class DartFunctionModel(
    val name: String,
    val type: String,
    val params: List<DartMemberModel>
)