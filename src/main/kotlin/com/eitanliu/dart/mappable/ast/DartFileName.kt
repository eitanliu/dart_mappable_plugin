package com.eitanliu.dart.mappable.ast

import com.eitanliu.dart.mappable.extensions.camelCaseToUnderscore
import com.eitanliu.dart.mappable.extensions.replaceNonAlphabeticNumber
import com.eitanliu.dart.mappable.extensions.underscoreToCamelCase

interface DartFileName {
    val camelCaseName: String

    val camelCaseSuffix: String

    val camelCaseNameAndSuffix: String

    val underscoreName: String

    val underscoreSuffix: String

    val underscoreNameAndSuffix: String

    val fileName: String

    class Impl(
        className: String,
        suffix: String,
    ) : Self {
        override val camelCaseName = className.keyToCamelCase(true)

        override val camelCaseSuffix = suffix
            .replaceNonAlphabeticNumber("_")
            .underscoreToCamelCase(true)

        override val camelCaseNameAndSuffix = "$camelCaseName$camelCaseSuffix"

        override val underscoreName = camelCaseName.camelCaseToUnderscore()

        override val underscoreSuffix = camelCaseSuffix.camelCaseToUnderscore()

        override val underscoreNameAndSuffix = camelCaseNameAndSuffix.camelCaseToUnderscore()

        override val fileName = "$underscoreNameAndSuffix.dart"
    }

    // self members and extension functions
    interface Self : DartFileName, Ext

    interface Ext {

        fun String.keyToFieldName(firstWord: Boolean = false): String {
            return trimStart('_').keyToCamelCase(firstWord)
        }

        fun String.keyToCamelCase(firstWord: Boolean = false): String {
            return replaceNonAlphabeticNumber("_")
                .underscoreToCamelCase(firstWord)
                .numberAddPrefix()
        }

        fun String.numberAddPrefix(prefix: String = "$"): String {
            val regex = Regex("^\\d.+$")
            return if (matches(regex)) "$prefix$this" else this
        }
    }
}