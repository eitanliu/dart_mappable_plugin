package com.eitanliu.dart.mappable.extensions


fun String.replaceFirstChar(capitalize: Boolean = true): String {
    val camelCase = this
    return if (capitalize) {
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

fun String.replaceNonAlphabeticNumber(replacement: String = "_"): String {
    val regex = Regex("[^a-zA-Z0-9]+")
    return replace(regex, replacement)
}
fun String.replaceSymbol(replacement: String = "_"): String {
    val regex = Regex("[\\x20-\\x2F\\x3A-\\x40\\x5B-\\x60\\x7B-\\x7E]+")
    return replace(regex, replacement)
}