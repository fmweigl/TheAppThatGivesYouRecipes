package com.example.yetanothermealsapp.randomrecipe.data

import com.example.yetanothermealsapp.randomrecipe.domain.Ingredient
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private const val MAX_INGREDIENTS = 20

internal fun JsonObject.toRecipe(): Recipe = Recipe(
    id = requireNotNull(string("idMeal")) { "Meal has no idMeal" },
    name = requireNotNull(string("strMeal")) { "Meal has no strMeal" },
    category = string("strCategory"),
    area = string("strArea"),
    instructions = string("strInstructions"),
    imageUrl = string("strMealThumb"),
    youtubeUrl = string("strYoutube"),
    sourceUrl = string("strSource"),
    tags = string("strTags")
        ?.split(',')
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        .orEmpty(),
    ingredients = (1..MAX_INGREDIENTS).mapNotNull { n ->
        string("strIngredient$n")?.let { name ->
            Ingredient(name = name, measure = string("strMeasure$n").orEmpty())
        }
    },
)

/** The trimmed string value of [key], or null when it is missing, JSON null, or blank. */
private fun JsonObject.string(key: String): String? =
    (get(key) as? JsonPrimitive)
        ?.takeIf { it.isString }
        ?.content
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
