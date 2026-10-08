package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.di

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.DATABASE_FILE_NAME
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.RecipeDatabase
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.RecipeDatabaseConstructor
import org.koin.core.module.Module
import org.koin.dsl.module

/** In the app's database directory; the `Context` comes from `androidContext()` in `MealsApplication`. */
internal actual val platformDatabaseModule: Module = module {
    single<RoomDatabase.Builder<RecipeDatabase>> {
        Room.databaseBuilder(get<Context>(), DATABASE_FILE_NAME) { RecipeDatabaseConstructor.initialize() }
    }
}
