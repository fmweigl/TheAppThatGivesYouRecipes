package io.github.fmweigl.theappthatgivesyourecipes.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.work.DisableCachingByDefault

/** The [configProperty] names of the upload key's settings, with their environment variables. */
private const val STORE_FILE = "releaseStoreFile"
private const val STORE_PASSWORD = "releaseStorePassword"
private const val KEY_ALIAS = "releaseKeyAlias"
private const val KEY_PASSWORD = "releaseKeyPassword"

private val SIGNING_PROPERTIES = mapOf(
    STORE_FILE to "RELEASE_STORE_FILE",
    STORE_PASSWORD to "RELEASE_STORE_PASSWORD",
    KEY_ALIAS to "RELEASE_KEY_ALIAS",
    KEY_PASSWORD to "RELEASE_KEY_PASSWORD",
)

/** Fails when the release signing settings are incomplete; release builds depend on it. */
@DisableCachingByDefault(because = "A check without outputs")
abstract class CheckReleaseSigning : DefaultTask() {

    // Only the names of the missing settings, so no password is stored as a task input.
    @get:Input
    abstract val missingProperties: ListProperty<String>

    @TaskAction
    fun check() {
        val missing = missingProperties.get()
        if (missing.isNotEmpty()) {
            throw GradleException(
                "Release builds are signed with the upload key, but these settings are missing: " +
                    missing.joinToString() + ". Set them in local.properties or as Gradle properties, " +
                    "or as the environment variables " +
                    missing.joinToString { SIGNING_PROPERTIES.getValue(it) } + ".",
            )
        }
    }
}

/**
 * Signs the `release` build type with the upload key from the [configProperty]s
 * `releaseStoreFile` (relative to the repository root, or absolute), `releaseStorePassword`,
 * `releaseKeyAlias` and `releaseKeyPassword`. Without all of them, release builds fail.
 */
fun ApplicationExtension.releaseSigning(project: Project) {
    val values = SIGNING_PROPERTIES
        .mapValues { (name, variable) -> project.configProperty(name, variable).orNull }
        .filterValues { it != null }
        .mapValues { it.value.orEmpty() }
    val missing = SIGNING_PROPERTIES.keys - values.keys

    if (missing.isEmpty()) {
        val release = signingConfigs.create("release") {
            storeFile = project.rootProject.file(values.getValue(STORE_FILE))
            storePassword = values.getValue(STORE_PASSWORD)
            keyAlias = values.getValue(KEY_ALIAS)
            keyPassword = values.getValue(KEY_PASSWORD)
        }
        buildTypes.getByName("release").signingConfig = release
    }

    val check = project.tasks.register<CheckReleaseSigning>("checkReleaseSigning") {
        description = "Fails if the release signing settings are incomplete."
        missingProperties.set(missing.toList())
    }
    // Every release task depends on preReleaseBuild.
    project.tasks.matching { it.name == "preReleaseBuild" }.configureEach { dependsOn(check) }
}
