import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class RestoreSnapshotTask extends DefaultTask {
    @TaskAction
    void restore() {
        def f = project.file('build.gradle')
        f.text = f.text.replaceAll(/(sheepDogTestVersion = ')(\d+)\.(\d+)(')/) { match, prefix, major, minor, suffix ->
            "${prefix}${major}.${(minor as int) + 1}-SNAPSHOT${suffix}"
        }
    }
}
