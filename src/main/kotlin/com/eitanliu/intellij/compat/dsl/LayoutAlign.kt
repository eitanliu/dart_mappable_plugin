@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.eitanliu.intellij.compat.dsl

import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent
import java.lang.Enum as JEnum

fun <C : JComponent> Cell<C>.layoutAlign(
    align: LayoutAlign
): Cell<C> {
    try {
        layoutAlignSince223(align)
    } catch (e: Throwable) {
        layoutAlignBefore223(align)
    }
    return this
}

private fun <C : JComponent> Cell<C>.layoutAlignSince223(layoutAlign: LayoutAlign): Cell<C> {
    val align = getAlignSince223(layoutAlign)
    align(align)
    return this
}

private fun getAlignSince223(align: LayoutAlign): Align {
    return when (align) {
        // com.intellij.ui.dsl.builder.Align.FILL.INSTANCE
        is LayoutAlignBoth -> {
            val alignX = getAlignSince223(align.alignX) as AlignX
            val alignY = getAlignSince223(align.alignY) as AlignY
            alignX + alignY
        }

        is LayoutAlignX -> {
            val alignX = when (align) {
                LayoutAlignX.LEFT -> AlignX.LEFT
                LayoutAlignX.CENTER -> AlignX.CENTER
                LayoutAlignX.RIGHT -> AlignX.RIGHT
                LayoutAlignX.FILL -> AlignX.FILL
            }
            alignX
        }

        is LayoutAlignY -> {
            val alignY = when (align) {
                LayoutAlignY.TOP -> AlignY.TOP
                LayoutAlignY.CENTER -> AlignY.CENTER
                LayoutAlignY.BOTTOM -> AlignY.BOTTOM
                LayoutAlignY.FILL -> AlignY.FILL
            }
            alignY
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun <C : JComponent> Cell<C>.layoutAlignBefore223(
    align: LayoutAlign
): Cell<C> {
    when (align) {
        is LayoutAlignBoth -> {
            this.layoutAlignBefore223(align.alignX)
            this.layoutAlignBefore223(align.alignY)
        }

        is LayoutAlignX -> {
            val clazz = Class.forName("com.intellij.ui.dsl.gridLayout.HorizontalAlign")
            val enumClass = clazz as Class<out Enum<*>>
            val horizontalAlign = when (align) {
                LayoutAlignX.LEFT -> JEnum.valueOf(enumClass, "LEFT")
                LayoutAlignX.CENTER -> JEnum.valueOf(enumClass, "CENTER")
                LayoutAlignX.RIGHT -> JEnum.valueOf(enumClass, "RIGHT")
                LayoutAlignX.FILL -> JEnum.valueOf(enumClass, "FILL")
            }
            val cellClass = this::class.java
            val alignMethod = cellClass.getMethod("horizontalAlign", enumClass)
            alignMethod.invoke(this, horizontalAlign)
            // horizontalAlign(horizontalAlign)
        }

        is LayoutAlignY -> {
            val clazz = Class.forName("com.intellij.ui.dsl.gridLayout.VerticalAlign")
            val enumClass = clazz as Class<out Enum<*>>
            val verticalAlign = when (align) {
                LayoutAlignY.TOP -> JEnum.valueOf(enumClass, "TOP")
                LayoutAlignY.CENTER -> JEnum.valueOf(enumClass, "CENTER")
                LayoutAlignY.BOTTOM -> JEnum.valueOf(enumClass, "BOTTOM")
                LayoutAlignY.FILL -> JEnum.valueOf(enumClass, "FILL")
            }
            val cellClass = this::class.java
            val alignMethod = cellClass.getMethod("verticalAlign", enumClass)
            alignMethod.invoke(this, verticalAlign)
            // verticalAlign(verticalAlign)
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