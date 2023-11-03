package com.eitanliu.intellij.compat.observable

interface CompatObservableProperty<T> {

    /**
     * may be removed API version 231.9011.34 (2023.1.2).
     */
    fun afterChange(listener: (T) -> Unit)
}