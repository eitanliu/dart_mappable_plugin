package com.eitanliu.dart.mappable.extensions


fun String.toCamelCase(capitalizeFirstWord: Boolean = true): String {
    val camelCase = this
    return if (capitalizeFirstWord) {
        camelCase.replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }
    } else {
        camelCase.replaceFirstChar { if (it.isUpperCase()) it.lowercase() else it.toString() }
    }
}

fun String.camelCaseToUnderscore(): String {
    val camelCase = this
    val words = mutableListOf<String>()
    var startIndex = 0
    for ((index, char) in camelCase.withIndex()) {
        if (char.isUpperCase()) {
            if (startIndex < index) {
                words.add(camelCase.substring(startIndex, index).lowercase())
            }
            startIndex = index
        }
    }
    if (startIndex < camelCase.length) {
        words.add(camelCase.substring(startIndex).lowercase())
    }
    return words.joinToString(separator = "_")
}

fun String.underscoreToCamelCase(capitalizeFirstWord: Boolean = false): String {
    val underscore = this
    val words = underscore.split("_")
    val camelCase = StringBuilder()
    for ((index, word) in words.withIndex()) {
        if (index == 0 && !capitalizeFirstWord) {
            camelCase.append(word)
        } else {
            camelCase.append(word.replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() })
        }
    }
    return camelCase.toString()
}