package igloo

import org.gradle.testkit.runner.GradleRunner
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class IglooPluginFunctionalTest {
    @Test
    fun `can run task`() {
        // Setup the test build
        val projectDir = File("build/functionalTest")
        projectDir.mkdirs()
        projectDir.resolve("src/").mkdir()

        // Add source files
        projectDir.resolve("src/einstein.txt").writeText("""
            Intellectual growth should commence at birth and cease only at death.
        """.trimIndent())
        projectDir.resolve("src/feynman.txt").writeText("""
            I was born not knowing and have had only a little time to change here and there.
        """.trimIndent())
        projectDir.resolve("src/oppenheimer.txt").writeText("""
            No man should escape our universities without knowing how little he knows.
        """.trimIndent())

        // Add gradle files
        projectDir.resolve("settings.gradle").writeText("")
        projectDir.resolve("build.gradle").writeText("""
            plugins {
                id('igloo.md5')
            }
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("createMD5")
        runner.withProjectDir(projectDir)
        val result = runner.build();

        // Verify the output
        assertTrue(result.output.contains("Generating MD5 for einstein.txt..."))
        assertTrue(result.output.contains("Generating MD5 for feynman.txt..."))
        assertTrue(result.output.contains("Generating MD5 for oppenheimer.txt..."))

        // Verify hashes
        listOf(
                Pair("build/md5/einstein.md5", "f78f8cd18d48176eb2577a627b303b10"),
                Pair("build/md5/feynman.md5", "83d20cc02ddb0dbead50d002de23bd4b"),
                Pair("build/md5/oppenheimer.md5", "8d9eb2c182df6d2ebf694d5013c052ef")
        ).forEach { tuple ->
            projectDir.resolve(tuple.first).let { file ->
                assertTrue(file.exists())
                assertEquals(file.readText(), tuple.second)
            }
        }
    }
}
