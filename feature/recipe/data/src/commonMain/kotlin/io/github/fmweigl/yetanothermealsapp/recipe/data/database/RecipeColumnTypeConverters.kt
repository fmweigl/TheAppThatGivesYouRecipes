package io.github.fmweigl.yetanothermealsapp.recipe.data.database

import androidx.room3.ColumnTypeConverter
import kotlinx.serialization.json.Json

/**
 * Stores lists as JSON text: they are only ever read as a whole with their recipe,
 * so separate tables would add joins without any benefit, and JSON keeps their order.
 */
internal class RecipeColumnTypeConverters {

    @ColumnTypeConverter
    fun stringsToJson(strings: List<String>): String = Json.encodeToString(strings)

    @ColumnTypeConverter
    fun jsonToStrings(json: String): List<String> = Json.decodeFromString(json)

    @ColumnTypeConverter
    fun ingredientsToJson(ingredients: List<StoredIngredient>): String = Json.encodeToString(ingredients)

    @ColumnTypeConverter
    fun jsonToIngredients(json: String): List<StoredIngredient> = Json.decodeFromString(json)
}
