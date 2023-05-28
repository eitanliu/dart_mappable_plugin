package com.eitanliu.dart.mappable.binding

import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.util.lockOrSkip
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.Cell
import java.util.concurrent.atomic.AtomicBoolean


fun <T : JBRadioButton> Cell<T>.bindSelected(
    property: ObservableMutableProperty<Boolean>,
    not: Boolean = false,
): Cell<T> {
    return applyToComponent { bind(property, not) }
}

fun <C : JBRadioButton> C.bind(property: ObservableMutableProperty<Boolean>, not: Boolean = false): C = apply {
    isSelected = if (not) !property.get() else property.get()
    val mutex = AtomicBoolean()
    property.afterChange {
        mutex.lockOrSkip {
            isSelected = if (not) !it else it
        }
    }
    addItemListener {
        mutex.lockOrSkip {
            property.set(if (not) !isSelected else isSelected)
        }
    }
}