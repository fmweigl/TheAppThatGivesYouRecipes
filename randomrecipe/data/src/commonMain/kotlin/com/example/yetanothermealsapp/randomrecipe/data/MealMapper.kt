package com.example.yetanothermealsapp.randomrecipe.data

import com.example.yetanothermealsapp.randomrecipe.domain.Ingredient
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private const val MAX_INGREDIENTS = 20

/** Maps a TheMealDB meal to a [Recipe], or returns null when it has no id or name. */
internal fun JsonObject.toRecipe(): Recipe? = Recipe(
    id = string("idMeal") ?: return null,
    name = string("strMeal") ?: return null,
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
