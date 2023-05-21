package com.eitanliu.dart.mappable.binding

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.ui.DocumentAdapter
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.event.DocumentEvent
import javax.swing.text.JTextComponent

fun JTextComponent.bind(property: GraphProperty<String>) {
    val mutex = AtomicBoolean()
    property.afterChange {
        mutex.lockOrSkip {
            text = it
        }
    }
    document.addDocumentListener(
        object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                mutex.lockOrSkip {
                    property.set(text)
                }
            }
        }
    )
}
