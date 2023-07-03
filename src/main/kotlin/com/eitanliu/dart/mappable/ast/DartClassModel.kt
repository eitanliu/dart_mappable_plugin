package com.eitanliu.dart.mappable.ast

data class DartClassModel(
    val name: String,
    val imports: List<DartImportModel>,
    val parts: List<DartPartModel>,
    val members: List<DartMemberModel>,
    val functions: List<DartFunctionModel>,
)

data class DartImportModel(
    val name: String,
    val alias: String? = null,
    val visibility: DartImportVisibility? = null,
)

data class DartImportVisibility(
    // show, hide
    val visible: String,
    val list: List<String>,
) {
    companion object {
        const val SHOW = "show"
        const val HIDE = "hide"
    }
}

data class DartPartModel(
    val part: String,
    val of: String,
    val slice: Boolean = false,
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
    val params: List<DartMemberModel>,
    val optionParams: List<DartMemberModel>? = null,
    val namedParams: List<DartMemberModel>? = null,
)