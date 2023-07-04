package com.eitanliu.dart.mappable.binding

import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.util.lockOrSkip
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.Cell
import java.util.concurrent.atomic.AtomicBoolean


fun <C : JBRadioButton, T> Cell<C>.bindSelected(
    property: ObservableMutableProperty<T>,
    expected: T,
): Cell<C> {
    return applyToComponent { bind(property, expected) }
}

fun <C : JBRadioButton, T> C.bind(property: ObservableMutableProperty<T>, expected: T): C = apply {
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