package com.eitanliu.dart.mappable

import com.eitanliu.dart.mappable.generator.CodeGenerator
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
}