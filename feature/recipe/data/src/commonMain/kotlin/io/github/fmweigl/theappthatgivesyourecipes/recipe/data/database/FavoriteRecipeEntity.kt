package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable

/** A favorite recipe with everything needed to show it offline. [savedAt] is in epoch milliseconds. */
@Entity(tableName = "favorite_recipe")
internal data class FavoriteRecipeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val instructions: String?,
    val imageUrl: String?,
    val youtubeUrl: String?,
    val sourceUrl: String?,
    val tags: List<String>,
    val ingredients: List<StoredIngredient>,
    val savedAt: Long,
)

/** An ingredient as stored in [FavoriteRecipeEntity.ingredients] (JSON, see [RecipeColumnTypeConverters]). */
@Serializable
internal data class StoredIngredient(
    val name: String,
    val measure: String,
)
