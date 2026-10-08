package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.di

import androidx.room3.Room
import androidx.room3.RoomDatabase
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.DATABASE_FILE_NAME
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.RecipeDatabase
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.RecipeDatabaseConstructor
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDomainMask

private const val DATABASE_DIRECTORY_NAME = "Database"

/**
 * In `Application Support/Database`, which is excluded from iCloud and computer backups: favorites
 * stay on the device, as on Android (see PRIVACY.md). Documents would also be shown in the Files app.
 */
internal actual val platformDatabaseModule: Module = module {
    single<RoomDatabase.Builder<RecipeDatabase>> {
        val path = requireNotNull(databaseDirectory().URLByAppendingPathComponent(DATABASE_FILE_NAME)?.path)
        Room.databaseBuilder(path) { RecipeDatabaseConstructor.initialize() }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun databaseDirectory(): NSURL {
    val fileManager = NSFileManager.defaultManager
    val applicationSupport = requireNotNull(
        fileManager.URLForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        ),
    ) { "No Application Support directory" }
    val directory = requireNotNull(applicationSupport.URLByAppendingPathComponent(DATABASE_DIRECTORY_NAME))
    fileManager.createDirectoryAtURL(directory, withIntermediateDirectories = true, attributes = null, error = null)
    // Covers every file SQLite creates in the directory (the database and its -wal, -shm and .lck files).
    directory.setResourceValue(true, forKey = NSURLIsExcludedFromBackupKey, error = null)
    return directory
}
