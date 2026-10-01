package com.example.yetanothermealsapp.randomrecipe.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Meals are kept as raw [JsonObject]s because each one spreads its ingredients
 * over the numbered `strIngredient1..20` / `strMeasure1..20` fields.
 */
@Serializable
internal data class MealsResponse(
    val meals: List<JsonObject>? = null,
)
