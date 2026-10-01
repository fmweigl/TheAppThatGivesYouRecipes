package io.github.fmweigl.yetanothermealsapp

import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.Ingredient
import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.Recipe
import java.io.File

/**
 * The store screenshots' recipes, written for them, with the illustrations from
 * `art/play-store/mock-recipes/` (copied into [directory]).
 */
internal fun screenshotRecipes(directory: File): List<Recipe> = listOf(
    Recipe(
        id = "screenshot-shakshuka",
        name = "Shakshuka",
        category = "Vegetarian",
        area = "Tunisian",
        imageUrl = File(directory, "shakshuka.png").toURI().toString(),
        ingredients = listOf(
            Ingredient("Olive Oil", "2 tbs"),
            Ingredient("Onion", "1 chopped"),
            Ingredient("Red Pepper", "1 sliced"),
            Ingredient("Garlic", "3 cloves"),
            Ingredient("Cumin", "1 tsp"),
            Ingredient("Paprika", "1 tsp"),
            Ingredient("Chopped Tomatoes", "800g"),
            Ingredient("Eggs", "5"),
            Ingredient("Feta", "50g"),
            Ingredient("Parsley", "Handful"),
        ),
        instructions = "Heat the oil in a large frying pan and soften the onion and pepper for " +
            "8 minutes. Stir in the garlic, cumin and paprika and cook for 1 minute.\n\n" +
            "Add the tomatoes, season and simmer for 10 minutes until the sauce thickens.\n\n" +
            "Make five hollows in the sauce and crack an egg into each. Cover and cook for 6–8 " +
            "minutes until the whites are set but the yolks are still runny.\n\n" +
            "Scatter over the feta and parsley and serve straight from the pan with bread.",
    ),
    Recipe(
        id = "screenshot-spaghetti",
        name = "Spaghetti al Pomodoro",
        category = "Pasta",
        area = "Italian",
        imageUrl = File(directory, "spaghetti.png").toURI().toString(),
        ingredients = listOf(
            Ingredient("Spaghetti", "400g"),
            Ingredient("Olive Oil", "3 tbs"),
            Ingredient("Garlic", "2 cloves"),
            Ingredient("Cherry Tomatoes", "500g"),
            Ingredient("Basil", "1 bunch"),
            Ingredient("Parmesan", "40g"),
            Ingredient("Salt", "To taste"),
        ),
        instructions = "Cook the spaghetti in well-salted boiling water until al dente.\n\n" +
            "Meanwhile, warm the oil with the sliced garlic, add the halved tomatoes and cook " +
            "for 10 minutes until they collapse into a sauce.\n\n" +
            "Toss the drained spaghetti through the sauce with a splash of the cooking water. " +
            "Serve topped with torn basil and shaved parmesan.",
    ),
)
