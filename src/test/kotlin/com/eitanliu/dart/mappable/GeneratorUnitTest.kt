package com.eitanliu.dart.mappable

import com.eitanliu.dart.mappable.generator.CodeGenerator
import com.eitanliu.dart.mappable.generator.DartGenerator
import com.eitanliu.dart.mappable.settings.Settings
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

    @Test
    fun jsonParse() {
        val classLoader = javaClass.classLoader
        val test01 = classLoader.getResourceAsStream("test01.json")!!
        val content = test01.bufferedReader().use { it.readText() }
        println("json: \n$content")
        val setting = Settings().apply {
            modelSuffix = "Vo"
            constructor = false
            nullable = true
            final = false
        }
        val generator = DartGenerator(setting, "Text01", content)
        val classes = generator.generatorClassesString()
        println("classes: \n$classes")
    }
}