package com.eitanliu.dart.mappable

import com.eitanliu.dart.mappable.ast.CodeGenerator
import com.eitanliu.dart.mappable.entity.PubspecEntity
import com.eitanliu.dart.mappable.generator.DartMappableGenerator
import com.eitanliu.dart.mappable.generator.JsonSerializableGenerator
import com.eitanliu.dart.mappable.settings.Implements
import com.eitanliu.dart.mappable.settings.Settings
import com.eitanliu.dart.mappable.utils.DependenciesUtils
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GeneratorUnitTest {
    @Test
    fun testGenerator() {
        val generator = CodeGenerator {

            writeln("import 'aa';")
            writeln()

            writeScoped("class A {", "}") {
                writeln("String a;")
                writeln()
                writeScoped("void b() {", "}") {
                    writeln("""print("test");""")
                }
            }
        }


        val actual = generator.builder.toString()
        println(actual)
        val expected = """
            import 'aa';

            class A {
              String a;

              void b() {
                print("test");
              }
            }

        """.trimIndent()

        assertEquals("generatorEquals", expected, actual)
    }

    private fun jsonParse(): String {
        val classLoader = javaClass.classLoader
        val test01 = classLoader.getResourceAsStream("test01.json")!!
        val content = test01.bufferedReader().use { it.readText() }
        println("json: \n$content")
        return content
    }

    @Test
    fun testDartMappable() {
        val content = jsonParse()

        val setting = Settings().apply {
            modelSuffix = "Vo"
            constructor = false
            nullable = true
            final = false

            enableJsonReflectable = false
            enableMixin = true
        }
        val generator = DartMappableGenerator(setting, "Text01", content)
        val classes = generator.buildString()
        println("classes: \n$classes")
    }

    @Test
    fun testJsonSerializable() {
        val content = jsonParse()

        val setting = Settings().apply {
            modelSuffix = "Vo"
            constructor = false
            nullable = true
            final = false
        }
        val generator = JsonSerializableGenerator(setting, "Text01", content)
        val classes = generator.buildString()
        println("classes: \n$classes")
    }

    @Test
    fun testDependencies() {

        val setting = Settings().apply {
            enableJsonReflectable = true
            implement = Implements.FREEZED
            freezedEnableJson = false
        }

        val pubspec = PubspecEntity(
            mapOf(
                "dependencies" to mapOf<String, Any>(),
                "dev_dependencies" to null,
            ),
            mapOf(
                "packages" to mapOf<String, Any>(),
            ),
        )

        println("dependencies => " + DependenciesUtils.checkDependencies(setting, pubspec).joinToString())
    }
}