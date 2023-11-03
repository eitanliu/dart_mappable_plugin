package com.eitanliu.dart.mappable.extensions

import com.intellij.openapi.vfs.VirtualFile
import org.yaml.snakeyaml.Yaml

fun <T> VirtualFile.loadYaml(): T = inputStream.use { Yaml().load(it) }