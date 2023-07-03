package com.eitanliu.dart.mappable.binding

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.applyToComponent
import java.util.concurrent.atomic.AtomicBoolean


fun <C : JBRadioButton, T> CellBuilder<C>.bindSelected(
    property: GraphProperty<T>,
    expected: T,
): CellBuilder<C> {
    return applyToComponent { bind(property, expected) }
}

fun <C : JBRadioButton, T> C.bind(property: GraphProperty<T>, expected: T): C = apply {
    isSelected = property.get() == expected
    val mutex = AtomicBoolean()
    property.afterChange {
        mutex.lockOrSkip {
            isSelected = it == expected
        }
    }
    addItemListener {
        mutex.lockOrSkip {
            if (property.get() != expected) property.set(expected)
        }
    }
}