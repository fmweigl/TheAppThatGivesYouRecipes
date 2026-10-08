package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.di

import androidx.room3.Room
import androidx.room3.RoomDatabase
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.DATABASE_FILE_NAME
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.RecipeDatabase
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.RecipeDatabaseConstructor
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

private const val APP_DIRECTORY_NAME = "TheAppThatGivesYouRecipes"

/** In the operating system's directory for application data. */
internal actual val platformDatabaseModule: Module = module {
    single<RoomDatabase.Builder<RecipeDatabase>> {
        val file = File(appDataDirectory(), DATABASE_FILE_NAME)
        file.parentFile.mkdirs()
        Room.databaseBuilder(file.absolutePath) { RecipeDatabaseConstructor.initialize() }
    }
}

private fun appDataDirectory(): File {
    val home = System.getProperty("user.home")
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.startsWith("windows") -> File(System.getenv("APPDATA") ?: home, APP_DIRECTORY_NAME)
        os.startsWith("mac") -> File(home, "Library/Application Support/$APP_DIRECTORY_NAME")
        else -> File(System.getenv("XDG_DATA_HOME") ?: "$home/.local/share", APP_DIRECTORY_NAME)
    }
}
