package io.github.fmweigl.yetanothermealsapp.recipe.data.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Ingredient
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe

/** A builder for a database that lives in memory only, configured like the app's. */
internal fun inMemoryRecipeDatabaseBuilder(): RoomDatabase.Builder<RecipeDatabase> =
    Room.inMemoryDatabaseBuilder { RecipeDatabaseConstructor.initialize() }

internal fun inMemoryRecipeDatabase(): RecipeDatabase = inMemoryRecipeDatabaseBuilder().buildRecipeDatabase()

/** A recipe with every field set, so a round trip through the database checks them all. */
internal fun testRecipe(id: String, name: String = "Recipe $id") = Recipe(
    id = id,
    name = name,
    category = "Dessert",
    area = "Canadian",
    instructions = "Preheat the oven.\r\nBake.",
    imageUrl = "https://www.themealdb.com/images/media/meals/$id.jpg",
    youtubeUrl = "https://www.youtube.com/watch?v=$id",
    sourceUrl = "https://example.com/$id",
    tags = listOf("Speciality", "Snack"),
    ingredients = listOf(
        Ingredient("Shortcrust Pastry", "375g"),
        Ingredient("Eggs", ""),
        Ingredient("Raisins", "100g"),
    ),
)
