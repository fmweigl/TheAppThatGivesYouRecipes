package io.github.fmweigl.yetanothermealsapp.recipe.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * Meals are kept as raw [JsonObject]s because each one spreads its ingredients
 * over the numbered `strIngredient1..20` / `strMeasure1..20` fields.
 */
@Serializable
internal data class MealsResponse(
    @SerialName("meals") private val mealsJson: JsonElement? = null,
) {
    /**
     * The meals; empty if there are none. TheMealDB then sends `null` or a message instead of an
     * array (`lookup.php` with an id that isn't a number answers `"Invalid ID"`).
     */
    val meals: List<JsonObject>
        get() = (mealsJson as? JsonArray)?.filterIsInstance<JsonObject>().orEmpty()
}
