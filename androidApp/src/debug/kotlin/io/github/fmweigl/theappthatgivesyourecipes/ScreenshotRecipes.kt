package io.github.fmweigl.theappthatgivesyourecipes

import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Ingredient
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import java.io.File

/**
 * The store screenshots' recipes, written for them, with the illustrations from
 * `art/play-store/mock-recipes/` (copied into [directory]). Four have "Tomato" in their name, so
 * searching for it shows a list of results.
 */
internal fun screenshotRecipes(directory: File): List<Recipe> = listOf(
    shakshuka(directory),
    spaghetti(directory),
    tomatoSoup(directory),
    stuffedTomatoes(directory),
    bruschetta(directory),
    tomatoTarteTatin(directory),
)

/** The recipes that start out as favorites, so the favorites tab has something to show. */
internal val screenshotFavoriteIds = setOf("screenshot-shakshuka", "screenshot-spaghetti")

private fun shakshuka(directory: File) = Recipe(
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
)

private fun spaghetti(directory: File) = Recipe(
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
)

private fun tomatoSoup(directory: File) = Recipe(
    id = "screenshot-tomato-soup",
    name = "Roasted Tomato Soup",
    category = "Starter",
    area = "British",
    imageUrl = File(directory, "tomato-soup.png").toURI().toString(),
    ingredients = listOf(
        Ingredient("Tomatoes", "1kg"),
        Ingredient("Red Onion", "1 quartered"),
        Ingredient("Garlic", "4 cloves"),
        Ingredient("Olive Oil", "3 tbs"),
        Ingredient("Vegetable Stock", "500ml"),
        Ingredient("Double Cream", "4 tbs"),
        Ingredient("Basil", "Handful"),
        Ingredient("Bread", "2 slices"),
    ),
    instructions = "Halve the tomatoes and roast them with the onion, garlic and oil at " +
        "200°C for 40 minutes until soft and charred at the edges.\n\n" +
        "Tip everything into a pan with the stock, simmer for 5 minutes and blend until " +
        "smooth. Season to taste.\n\n" +
        "Cube the bread and toast it in a dry pan. Serve the soup with a swirl of cream, " +
        "the croutons and torn basil.",
)

private fun stuffedTomatoes(directory: File) = Recipe(
    id = "screenshot-stuffed-tomatoes",
    name = "Stuffed Tomatoes",
    category = "Vegetarian",
    area = "Greek",
    imageUrl = File(directory, "stuffed-tomatoes.png").toURI().toString(),
    ingredients = listOf(
        Ingredient("Large Tomatoes", "6"),
        Ingredient("Rice", "150g"),
        Ingredient("Onion", "1 chopped"),
        Ingredient("Olive Oil", "4 tbs"),
        Ingredient("Dill", "1 bunch"),
        Ingredient("Mint", "Handful"),
        Ingredient("Pine Nuts", "30g"),
    ),
    instructions = "Cut the tops off the tomatoes and keep them as lids. Scoop out the " +
        "flesh, chop it and keep the juice.\n\n" +
        "Soften the onion in half the oil, stir in the rice, the chopped tomato, the herbs " +
        "and the pine nuts and cook for 5 minutes.\n\n" +
        "Fill the tomatoes two thirds full, put the lids on and stand them in a baking dish. " +
        "Pour the juice and the rest of the oil around them and bake at 180°C for an hour, " +
        "until the rice is tender.",
)

private fun bruschetta(directory: File) = Recipe(
    id = "screenshot-bruschetta",
    name = "Tomato Bruschetta",
    category = "Starter",
    area = "Italian",
    imageUrl = File(directory, "bruschetta.png").toURI().toString(),
    ingredients = listOf(
        Ingredient("Ciabatta", "1 loaf"),
        Ingredient("Ripe Tomatoes", "4"),
        Ingredient("Garlic", "1 clove"),
        Ingredient("Basil", "Handful"),
        Ingredient("Olive Oil", "4 tbs"),
        Ingredient("Sea Salt", "Pinch"),
    ),
    instructions = "Dice the tomatoes, mix them with half the oil and the torn basil, season " +
        "and leave for 10 minutes.\n\n" +
        "Slice the bread and toast it on a hot griddle until golden. Rub each slice with " +
        "the cut garlic.\n\n" +
        "Spoon the tomatoes onto the toast, drizzle with the rest of the oil and finish " +
        "with a little sea salt.",
)

private fun tomatoTarteTatin(directory: File) = Recipe(
    id = "screenshot-tomato-tarte-tatin",
    name = "Tomato Tarte Tatin",
    category = "Vegetarian",
    area = "French",
    imageUrl = File(directory, "tomato-tarte-tatin.png").toURI().toString(),
    ingredients = listOf(
        Ingredient("Cherry Tomatoes", "500g"),
        Ingredient("Puff Pastry", "320g"),
        Ingredient("Butter", "25g"),
        Ingredient("Sugar", "2 tbs"),
        Ingredient("Balsamic Vinegar", "2 tbs"),
        Ingredient("Thyme", "4 sprigs"),
    ),
    instructions = "Melt the butter and sugar in an ovenproof pan until golden, then stir in " +
        "the vinegar and the thyme leaves.\n\n" +
        "Pack the halved tomatoes into the caramel, cut side down, and cook for 5 minutes.\n\n" +
        "Lay the pastry over the top, tuck in the edges and bake at 200°C for 25 minutes " +
        "until puffed and golden. Leave for 5 minutes, then turn out onto a plate.",
)
