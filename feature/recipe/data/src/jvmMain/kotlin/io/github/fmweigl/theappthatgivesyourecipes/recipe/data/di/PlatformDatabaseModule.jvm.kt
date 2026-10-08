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

/** The directory's name before the app was renamed; its favorites move to the new one. */
private const val LEGACY_APP_DIRECTORY_NAME = "YetAnotherMealsApp"

/** In the operating system's directory for application data. */
internal actual val platformDatabaseModule: Module = module {
    single<RoomDatabase.Builder<RecipeDatabase>> {
        val directory = appDataDirectory(APP_DIRECTORY_NAME)
        moveLegacyDirectory(from = appDataDirectory(LEGACY_APP_DIRECTORY_NAME), to = directory)
        val file = File(directory, DATABASE_FILE_NAME)
        file.parentFile.mkdirs()
        Room.databaseBuilder(file.absolutePath) { RecipeDatabaseConstructor.initialize() }
    }
}

/** Keeps the favorites of installations from before the rename. If the move fails, the app starts empty. */
private fun moveLegacyDirectory(from: File, to: File) {
    if (from.isDirectory && !to.exists()) {
        to.parentFile.mkdirs()
        from.renameTo(to)
    }
}

private fun appDataDirectory(name: String): File {
    val home = System.getProperty("user.home")
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.startsWith("windows") -> File(System.getenv("APPDATA") ?: home, name)
        os.startsWith("mac") -> File(home, "Library/Application Support/$name")
        else -> File(System.getenv("XDG_DATA_HOME") ?: "$home/.local/share", name)
    }
}
