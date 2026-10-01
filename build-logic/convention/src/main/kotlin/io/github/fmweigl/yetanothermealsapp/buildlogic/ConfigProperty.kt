package io.github.fmweigl.yetanothermealsapp.buildlogic

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import java.util.Properties

/**
 * A build setting that may be secret (API keys, signing credentials), from the first of: the
 * Gradle property [name] (e.g. in `~/.gradle/gradle.properties`), [name] in the root
 * `local.properties` (not committed), the environment variable [environmentVariable] (CI).
 * Blank values count as missing, and values are trimmed.
 */
fun Project.configProperty(name: String, environmentVariable: String): Provider<String> {
    val localProperties = rootProject.layout.projectDirectory.file("local.properties")
    val fromLocalProperties = providers.fileContents(localProperties).asText.map { text ->
        Properties().apply { load(text.reader()) }.getProperty(name).orEmpty()
    }
    return providers.gradleProperty(name)
        .orElse(fromLocalProperties.filter { it.isNotBlank() })
        .orElse(providers.environmentVariable(environmentVariable))
        .map(String::trim)
        .filter { it.isNotEmpty() }
}
