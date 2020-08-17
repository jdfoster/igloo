package igloo

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class IglooPluginTest {
    @Test
    fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("igloo.md5")

        // Verify the result
        assertNotNull(project.tasks.findByName("createMD5"))
    }
}
