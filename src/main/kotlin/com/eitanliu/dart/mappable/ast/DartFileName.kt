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

    class Default(
        className: String,
        suffix: String,
    ) : DartFileName, Extensions {
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

    interface Extensions {

        fun String.keyToCamelCase(capitalizeFirstWord: Boolean = false): String {
            return replaceNonAlphabeticNumber("_")
                .underscoreToCamelCase(capitalizeFirstWord)
                .numberAddPrefix()
        }

        fun String.numberAddPrefix(prefix: String = "$"): String {
            val regex = Regex("^\\d.+$")
            return if (matches(regex)) "$prefix$this" else this
        }
    }
}