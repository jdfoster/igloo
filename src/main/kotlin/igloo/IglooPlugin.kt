package igloo

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import javax.inject.Inject

interface MD5WorkParameters : WorkParameters {
    val sourceFile: RegularFileProperty
    val md5File: RegularFileProperty
}

abstract class GenerateMD5 : WorkAction<MD5WorkParameters> {
    override fun execute() {
        val sourceFile = parameters.sourceFile.asFile.get()
        val md5File = parameters.md5File.asFile.get()
        val stream = FileInputStream(sourceFile)
        println("Generating MD5 for ${sourceFile.name}...")
        FileUtils.writeStringToFile(md5File, DigestUtils.md5Hex(stream), Charset.defaultCharset())
    }
}

open class CreateMD5 @Inject constructor(private val workerExecutor: WorkerExecutor) : SourceTask() {
    val destDirectory = project.objects.directoryProperty()

    @TaskAction
    fun createHashes() {
        val workQueue = workerExecutor.noIsolation()

        source.files.forEach { srcFile ->
            val md5File = destDirectory.file(srcFile.nameWithoutExtension + ".md5")
            workQueue.submit(GenerateMD5::class.java) {
                it.sourceFile.set(srcFile)
                it.md5File.set(md5File)
            }
        }
    }
}

@Suppress("unused")
class IglooPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("createMD5", CreateMD5::class.java) { task ->
            task.destDirectory.set(project.layout.buildDirectory.dir("md5"))
            task.setSource(File("src"))

            task.doFirst {
                println("Attempting to calculate file checksums.")
            }

            task.doLast {
                println("Finished calculating file checksums.")
            }
        }
    }
}
