package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.di

import org.koin.core.module.Module

/** Provides the platform's `RoomDatabase.Builder<RecipeDatabase>`, which decides where the database file lives. */
internal expect val platformDatabaseModule: Module
