package com.eitanliu.intellij.compat.binding

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.applyToComponent
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.ButtonGroup


fun <C : JBRadioButton, T> CellBuilder<C>.bindSelected(
    property: GraphProperty<T>, expected: T,
    group: ButtonGroup? = null,
): CellBuilder<C> {
    return applyToComponent {
        bind(property, expected, group)
    }
}

fun <C : JBRadioButton, T> C.bind(
    property: GraphProperty<T>, expected: T,
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