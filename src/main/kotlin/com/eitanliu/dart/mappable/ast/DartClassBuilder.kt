package com.eitanliu.dart.mappable.ast

import com.eitanliu.dart.mappable.generator.CodeGenerator

interface DartClassBuilder : DartFileName {
    fun generator(): CodeGenerator

    interface Extensions {}
}

