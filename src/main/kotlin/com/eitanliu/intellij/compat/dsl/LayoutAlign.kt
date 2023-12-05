package com.eitanliu.intellij.compat.dsl

import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import javax.swing.JComponent


fun <C : JComponent> Cell<C>.layoutAlign(
    align: LayoutAlign
): Cell<C> {
    try {
        layoutAlignBefore223(align)
    } catch (e: Throwable) {
    }
    return this
}

private fun <C : JComponent> Cell<C>.layoutAlignBefore223(
    align: LayoutAlign
): Cell<C> {
    when (align) {
        is LayoutAlignBoth -> {
            this.layoutAlignBefore223(align.alignX)
            this.layoutAlignBefore223(align.alignY)
        }

        is LayoutAlignX -> {
            val horizontalAlign = when (align) {
                LayoutAlignX.LEFT -> HorizontalAlign.LEFT
                LayoutAlignX.CENTER -> HorizontalAlign.CENTER
                LayoutAlignX.RIGHT -> HorizontalAlign.RIGHT
                LayoutAlignX.FILL -> HorizontalAlign.FILL
            }
            horizontalAlign(horizontalAlign)
        }

        is LayoutAlignY -> {
            val verticalAlign = when (align) {
                LayoutAlignY.TOP -> VerticalAlign.TOP
                LayoutAlignY.CENTER -> VerticalAlign.CENTER
                LayoutAlignY.BOTTOM -> VerticalAlign.BOTTOM
                LayoutAlignY.FILL -> VerticalAlign.FILL
            }
            verticalAlign(verticalAlign)
        }
    }
    return this
}

/**
 * https://plugins.jetbrains.com/docs/intellij/kotlin-ui-dsl-version-2.html#cell-align
 * since 2022.3 align(AlignX + AlignY) [Align.kt](https://github.com/JetBrains/intellij-community/blob/223.7126/platform/platform-impl/src/com/intellij/ui/dsl/builder/Align.kt)
 * before 2022.3 horizontalAlign(HorizontalAlign), verticalAlign(VerticalAlign) [Constraints.kt](https://github.com/JetBrains/intellij-community/blob/223.7126/platform/platform-impl/src/com/intellij/ui/dsl/gridLayout/Constraints.kt)
 */
sealed interface LayoutAlign {
    companion object {
        @JvmField
        val FILL: LayoutAlign = LayoutAlignX.FILL + LayoutAlignY.FILL

        @JvmField
        val CENTER: LayoutAlign = LayoutAlignX.CENTER + LayoutAlignY.CENTER
    }
}

sealed interface LayoutAlignX : LayoutAlign {
    object LEFT : LayoutAlignX
    object CENTER : LayoutAlignX
    object RIGHT : LayoutAlignX
    object FILL : LayoutAlignX
}

sealed interface LayoutAlignY : LayoutAlign {
    object TOP : LayoutAlignY
    object CENTER : LayoutAlignY
    object BOTTOM : LayoutAlignY
    object FILL : LayoutAlignY
}

operator fun LayoutAlignX.plus(alignY: LayoutAlignY): LayoutAlign {
    return LayoutAlignBoth(this, alignY)
}

operator fun LayoutAlignY.plus(alignX: LayoutAlignX): LayoutAlign {
    return LayoutAlignBoth(alignX, this)
}

internal class LayoutAlignBoth(val alignX: LayoutAlignX, val alignY: LayoutAlignY) : LayoutAlign