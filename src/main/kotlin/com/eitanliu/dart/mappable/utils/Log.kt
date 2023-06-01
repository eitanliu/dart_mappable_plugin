package com.eitanliu.dart.mappable.utils

import com.intellij.openapi.diagnostic.Logger

object Log {

    private val log = Logger.getInstance(Log::class.java)

    fun e() {
        val info = getInfo()
        log.error(info.call)
    }

    fun e(message: Any?) {
        val info = getInfo()
        log.error("${info.call} ${message?.toString() ?: "null"}")
    }

    fun e(vararg messages: Any?) {
        val info = getInfo()
        val length = maxOf(info.call.length, 64)

        log.error("=".repeat(length))
        log.error(info.call)
        log.error("▁".repeat(length))
        messages.forEach { log.error(it.toString()) }
        log.error("▔".repeat(length))
    }

    fun e(message: Any?, throwable: Throwable) {
        val info = getInfo()
        log.error("${info.call} ${message?.toString() ?: "null"}", throwable)
    }

    fun w() {
        val info = getInfo()
        log.warn(info.call)
    }

    fun w(message: Any?) {
        val info = getInfo()
        log.warn("${info.call} ${message?.toString() ?: "null"}")

    }

    fun w(vararg messages: Any?) {
        val info = getInfo()
        val length = maxOf(info.call.length, 64)

        log.warn("=".repeat(length))
        log.warn(info.call)
        log.warn("▁".repeat(length))
        messages.forEach { log.warn(it.toString()) }
        log.warn("▔".repeat(length))
    }

    fun i() {
        val info = getInfo()
        log.info(info.call)
    }

    fun i(message: Any?) {
        val info = getInfo()
        log.info("${info.call} ${message?.toString() ?: "null"}")
    }

    private fun getInfo(): LogCallerInfo {
        val list = Thread.currentThread().stackTrace
        val traceElement = list.first {
            it.className !in arrayOf(Thread::class.qualifiedName, Log::class.qualifiedName)
        }
        val method = traceElement.methodName
        val line = "${traceElement.lineNumber}"
        val clazz = traceElement.className
        val file = traceElement.fileName
        return LogCallerInfo(file, clazz, method, line)
    }

    internal class LogCallerInfo(
        val file: String, val clazz: String, val method: String, val line: String
    ) {
        val sample = clazz.substringAfterLast('.')
        val call get() = "$method($file:$line)"
    }
}
