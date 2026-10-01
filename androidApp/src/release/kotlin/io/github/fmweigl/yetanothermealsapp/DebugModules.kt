package io.github.fmweigl.yetanothermealsapp

import android.content.Context
import org.koin.core.module.Module

/** Release builds add no modules; the debug build's version enables the screenshot mode. */
@Suppress("UnusedParameter") // Same signature as the debug build's version.
internal fun debugModules(context: Context): List<Module> = emptyList()
