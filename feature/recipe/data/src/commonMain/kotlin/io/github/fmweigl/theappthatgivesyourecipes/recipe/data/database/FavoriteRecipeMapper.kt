package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database

import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Ingredient
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe

internal fun Recipe.toFavoriteRecipeEntity(savedAt: Long) = FavoriteRecipeEntity(
    id = id,
    name = name,
    category = category,
    area = area,
    instructions = instructions,
    imageUrl = imageUrl,
    youtubeUrl = youtubeUrl,
    sourceUrl = sourceUrl,
    tags = tags,
    ingredients = ingredients.map { StoredIngredient(name = it.name, measure = it.measure) },
    savedAt = savedAt,
)

internal fun FavoriteRecipeEntity.toRecipe() = Recipe(
    id = id,
    name = name,
    category = category,
    area = area,
    instructions = instructions,
    imageUrl = imageUrl,
    youtubeUrl = youtubeUrl,
    sourceUrl = sourceUrl,
    tags = tags,
    ingredients = ingredients.map { Ingredient(name = it.name, measure = it.measure) },
)
