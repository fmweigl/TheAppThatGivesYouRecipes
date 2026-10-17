package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe

/**
 * The words [toShareText] puts around a recipe, resolved from Compose resources by the caller, so
 * the text can be built (and tested) without Compose. [sourceLine] is "Source: ‹url›" for the
 * recipe's `sourceUrl`, or null if it has none.
 */
internal class ShareTextLabels(
    val ingredients: String,
    val instructions: String,
    val sourceLine: String?,
    val sharedFrom: String,
)

/**
 * The recipe as plain text for sharing: name, "Category · Area", the ingredients as a list, the
 * instructions, the source link and a line naming the app. Sections the recipe doesn't have are
 * left out. Line breaks are `\n`, also in instructions TheMealDB sends with `\r\n`.
 */
internal fun Recipe.toShareText(labels: ShareTextLabels): String {
    val sections = buildList {
        add(listOfNotNull(name, listOfNotNull(category, area).joinToString(" · ").ifEmpty { null }))
        if (ingredients.isNotEmpty()) {
            add(listOf(labels.ingredients) + ingredients.map { "• " + "${it.measure.trim()} ${it.name.trim()}".trim() })
        }
        instructions?.normalizeLineBreaks()?.ifBlank { null }?.let { add(listOf(labels.instructions, it)) }
        add(listOfNotNull(labels.sourceLine, labels.sharedFrom))
    }
    return sections.joinToString("\n\n") { it.joinToString("\n") }
}

private fun String.normalizeLineBreaks() = replace("\r\n", "\n").replace('\r', '\n').trim()
