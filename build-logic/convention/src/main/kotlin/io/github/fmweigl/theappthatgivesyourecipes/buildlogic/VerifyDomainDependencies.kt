package io.github.fmweigl.theappthatgivesyourecipes.buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin

/** Groups a domain module must not depend on in commonMain, directly or transitively (subgroups included). */
private val BANNED_GROUPS = listOf("io.ktor", "io.insert-koin", "androidx", "org.jetbrains.androidx", "org.jetbrains.compose")

private fun isBanned(group: String): Boolean = BANNED_GROUPS.any { group == it || group.startsWith("$it.") }

/** Fails if a domain module's commonMain depends on Ktor, Koin, AndroidX or Compose. */
abstract class VerifyDomainDependencies : DefaultTask() {

    @get:Input
    abstract val projectPath: Property<String>

    /** One entry per offending commonMain dependency, e.g. `project :core:network (brings in io.ktor:ktor-client-core:3.6.0)`. */
    @get:Input
    abstract val violations: ListProperty<String>

    @TaskAction
    fun verify() {
        val found = violations.get().distinct().sorted()
        if (found.isNotEmpty()) {
            throw GradleException(
                "'${projectPath.get()}' is a domain module and must not depend on Ktor, Koin, AndroidX or Compose. " +
                    "Found in commonMain:\n" + found.joinToString("\n") { "  - $it" },
            )
        }
    }
}

internal fun Project.registerVerifyDomainDependencies() {
    val verify = tasks.register<VerifyDomainDependencies>("verifyDomainDependencies") {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Checks that commonMain doesn't depend on Ktor, Koin, AndroidX or Compose."
        projectPath.set(this@registerVerifyDomainDependencies.path)

        // Declared in any commonMain bucket, including runtimeOnly, which the compile classpath misses.
        val declared = configurations.matching { it.name.startsWith("commonMain") && !it.isCanBeResolved }
        violations.addAll(provider {
            declared.flatMap { configuration ->
                configuration.dependencies.withType(ExternalModuleDependency::class.java)
                    .filter { isBanned(it.group.orEmpty()) }
                    .map { "${it.group}:${it.name}:${it.version.orEmpty()}" }
            }
        })
        // Resolved, so a banned library that comes in through another dependency is caught too.
        violations.addAll(configurations.named("metadataCommonMainCompileClasspath").flatMap { configuration ->
            configuration.incoming.resolutionResult.rootComponent.map(::violationsOf)
        })
    }
    tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) { dependsOn(verify) }
}

/** For each direct dependency of [root]: itself if banned, otherwise the first banned components it brings in. */
private fun violationsOf(root: ResolvedComponentResult): List<String> =
    root.directDependencies().mapNotNull { direct ->
        if (direct.id.isBanned()) {
            direct.id.displayName
        } else {
            direct.firstBannedDependencies().takeIf { it.isNotEmpty() }
                ?.let { banned -> "${direct.id.displayName} (brings in ${banned.joinToString { it.displayName }})" }
        }
    }

private fun ResolvedComponentResult.directDependencies(): List<ResolvedComponentResult> =
    dependencies.filterIsInstance<ResolvedDependencyResult>().map { it.selected }.distinct()

private fun ComponentIdentifier.isBanned(): Boolean = this is ModuleComponentIdentifier && isBanned(group)

/** Banned components reachable from this one, without descending into banned components. */
private fun ResolvedComponentResult.firstBannedDependencies(): List<ComponentIdentifier> {
    val seen = mutableSetOf(this)
    val queue = ArrayDeque(directDependencies())
    val banned = linkedSetOf<ComponentIdentifier>()
    while (queue.isNotEmpty()) {
        val component = queue.removeFirst()
        if (!seen.add(component)) continue
        if (component.id.isBanned()) banned += component.id else queue += component.directDependencies()
    }
    return banned.toList()
}
