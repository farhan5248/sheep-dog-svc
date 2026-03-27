import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class StripSnapshotTask extends DefaultTask {
    @TaskAction
    void strip() {
        def f = project.file('build.gradle')
        f.text = f.text.replaceAll(/(\d+\.\d+(\.\d+)?)-SNAPSHOT/, '$1')
    }
}
