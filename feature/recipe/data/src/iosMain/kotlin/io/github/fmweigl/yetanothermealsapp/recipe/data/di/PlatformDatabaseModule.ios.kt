package io.github.fmweigl.yetanothermealsapp.recipe.data.di

import androidx.room3.Room
import androidx.room3.RoomDatabase
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.DATABASE_FILE_NAME
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.RecipeDatabase
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.RecipeDatabaseConstructor
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/** In the app's Documents directory. */
internal actual val platformDatabaseModule: Module = module {
    single<RoomDatabase.Builder<RecipeDatabase>> {
        Room.databaseBuilder("${documentDirectory()}/$DATABASE_FILE_NAME") { RecipeDatabaseConstructor.initialize() }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(directory?.path) { "No Documents directory" }
}
