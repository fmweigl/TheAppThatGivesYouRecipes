package io.github.fmweigl.yetanothermealsapp.buildlogic

import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Static analysis with detekt, using the shared `config/detekt/detekt.yml` on top of detekt's defaults.
 * The `detekt` task (run by `check`) covers every source set under `src/`, without type resolution.
 */
class DetektConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("dev.detekt")

        extensions.configure<DetektExtension> {
            buildUponDefaultConfig.set(true)
            config.setFrom(rootProject.layout.projectDirectory.file("config/detekt/detekt.yml"))
            parallel.set(true)
            // detekt's defaults (src/main/kotlin, src/test/kotlin) miss KMP source sets such as src/commonMain/kotlin.
            source.setFrom(layout.projectDirectory.dir("src"))
        }
    }
}
