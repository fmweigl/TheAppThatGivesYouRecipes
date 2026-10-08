package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.preview.MealsPreview
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Ingredient
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe

/** A recipe written for previews (not from TheMealDB). */
internal val previewRecipe = Recipe(
    id = "preview",
    name = "Shakshuka",
    category = "Vegetarian",
    area = "Tunisian",
    instructions = "Soften the onion and pepper in olive oil, then add garlic, cumin and paprika.\n\n" +
        "Pour in the tomatoes and simmer until thick. Make six hollows, crack an egg into each, cover and " +
        "cook until the whites are set. Scatter with parsley and serve with bread.",
    ingredients = listOf(
        Ingredient("Olive oil", "2 tbsp"),
        Ingredient("Onion", "1, sliced"),
        Ingredient("Red pepper", "1, sliced"),
        Ingredient("Garlic", "2 cloves"),
        Ingredient("Cumin", "1 tsp"),
        Ingredient("Paprika", "1 tsp"),
        Ingredient("Chopped tomatoes", "800 g"),
        Ingredient("Eggs", "6"),
        Ingredient("Parsley", "1 handful"),
    ),
)

/** [MealsPreview] in which every image is a plain color, since previews can't load images. */
@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun RecipePreview(content: @Composable () -> Unit) {
    MealsPreview {
        val imageColor = MaterialTheme.colorScheme.secondaryContainer.toArgb()
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { ColorImage(imageColor) },
            content = content,
        )
    }
}
