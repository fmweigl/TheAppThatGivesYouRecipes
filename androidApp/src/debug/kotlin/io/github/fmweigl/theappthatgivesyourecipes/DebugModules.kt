package io.github.fmweigl.theappthatgivesyourecipes

import android.content.Context
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository.FavoritesRepository
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository.RecipeRepository
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

/**
 * Screenshot mode for the store screenshots: when the app's files contain a `screenshot-mode`
 * directory (created over adb with `run-as`, see CLAUDE.md, "Store listing"), the random recipe
 * tab shows [screenshotRecipes] with the images from that directory instead of TheMealDB's
 * recipes, whose photos may not be used in store listings, and the favorites tab lists the same
 * recipes (kept in memory, not in the database). Debug builds only.
 */
internal fun debugModules(context: Context): List<Module> {
    val directory = File(context.filesDir, "screenshot-mode")
    if (!directory.isDirectory) return emptyList()
    val recipes = screenshotRecipes(directory)
    return listOf(
        module {
            single<RecipeRepository> { ScreenshotRecipeRepository(recipes) }
            single<FavoritesRepository> { ScreenshotFavoritesRepository(recipes) }
        },
    )
}
