package com.eitanliu.dart.mappable.ast

class CodeGenerator(
    /**
     * String used for newlines (ex "\n").
     */
    val newline: String = "\n",
    /**
     * String used to represent a tab.
     */
    val tab: String = "  ",
    val builder: StringBuilder = StringBuilder(),
    block: CodeGenerator.() -> Unit,
) {

    private var count = 0


    init {
        block()
    }


    /**
     * Increase the indentation level.
     */
    fun inc(level: Int = 1) {
        count += level
    }

    /**
     * Decrement the indentation level.
     */
    fun dec(level: Int = 1) {
        count -= level
    }

    /**
     * Returns the String representing the current indentation.
     */
    fun indent(): String {
        var result = ""
        for (i in 0 until count) {
            result += tab
        }
        return result
    }

    /**
     * Replaces the newlines and tabs of input and adds it to the stream.
     */
    fun format(
        input: String,
        leadingSpace: Boolean = true,
        trailingNewline: Boolean = true,
    ) {
        val lines = input.split('\n')
        for (i in lines.indices) {
            val line = lines[i]
            if (i == 0 && !leadingSpace) {
                addln(line.replace("\t", tab))
            } else if (i == lines.size - 1 && !trailingNewline) {
                write(line.replace("\t", tab))
            } else {
                writeln(line.replace("\t", tab))
            }
        }
    }

    /**
     * Scoped increase of the ident level.  For the execution of [block] the
     * indentation will be incremented.
     */
    fun scoped(
        begin: String?,
        end: String?,
        addTrailingNewline: Boolean = true,
        block: CodeGenerator.() -> Unit,
    ) {
        if (begin != null) {
            builder.append(begin + newline)
        }
        nest(1, block)
        if (end != null) {
            builder.append(indent() + end)
            if (addTrailingNewline) {
                builder.append(newline)
            }
        }
    }

    /**
     * Like `scoped` but writes the current indentation level.
     */
    fun writeScoped(
        begin: String?,
        end: String,
        addTrailingNewline: Boolean = true,
        block: CodeGenerator.() -> Unit,
    ) {
        scoped(indent() + (begin ?: ""), end, addTrailingNewline, block)
    }

    /**
     * Scoped increase of the ident level.  For the execution of [block] the
     * indentation will be incremented by the given amount.
     */
    fun nest(count: Int, block: CodeGenerator.() -> Unit) {
        inc(count)
        block()
        dec(count)
    }

    /**
     * Add [text] with indentation and a newline.
     */
    fun writeln(text: String = "") {
        if (text.isEmpty()) {
            builder.append(newline)
        } else {
            builder.append(indent() + text + newline)
        }
    }

    /**
     * Add [text] with indentation.
     */
    fun write(text: String) {
        builder.append(indent() + text)
    }

    /**
     * Add [text] with a newline.
     */
    fun addln(text: String) {
        builder.append(text + newline)
    }

    /**
     * Just adds [text].
     */
    fun add(text: String) {
        builder.append(text)
    }
}