package com.eitanliu.dart.mappable.extensions

import com.intellij.openapi.vfs.VirtualFile
import io.flutter.pub.PubRoot

fun List<PubRoot>.filterInContent(file: VirtualFile): List<PubRoot> {
    val path = file.path
    return asSequence()
        .filter { path.contains(it.root.path) }
        .sortedByDescending { it.path }
        .toList()
}