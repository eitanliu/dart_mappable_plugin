package com.eitanliu.dart.mappable.binding

import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.util.lockOrSkip
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.Cell
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.ButtonGroup


fun <C : JBRadioButton, T> Cell<C>.bindSelected(
    property: ObservableMutableProperty<T>, expected: T,
    group: ButtonGroup? = null,
): Cell<C> {
    return applyToComponent {
        bind(property, expected, group)
    }
}

fun <C : JBRadioButton, T> C.bind(
    property: ObservableMutableProperty<T>, expected: T,
    group: ButtonGroup? = null,
): C = apply {
    if (group != null) model.group = group
    isSelected = property.get() == expected
    val mutex = AtomicBoolean()
    property.afterChange {
        mutex.lockOrSkip {
            isSelected = it == expected
        }
    }
    addItemListener {
        mutex.lockOrSkip {
            if (isSelected) property.set(expected)
        }
    }
}