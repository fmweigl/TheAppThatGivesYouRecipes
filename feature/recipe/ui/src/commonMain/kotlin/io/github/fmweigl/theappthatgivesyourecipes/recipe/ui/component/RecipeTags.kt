package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.Res
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.tags
import org.jetbrains.compose.resources.stringResource

private val TagSpacing = 8.dp

/** The tags as TheMealDB writes them, in its order, without repeats that differ only in case. */
internal fun List<String>.distinctTags(): List<String> = distinctBy { it.lowercase() }

/**
 * Small chips with a recipe's tags, wrapping onto as many lines as needed. They do nothing when
 * tapped, so they aren't buttons; for screen readers the row is one text, "Tags: Spicy, Breakfast",
 * which is neither focusable nor clickable.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RecipeTags(tags: List<String>, modifier: Modifier = Modifier) {
    val label = stringResource(Res.string.tags, tags.joinToString(", "))
    FlowRow(
        modifier = modifier.clearAndSetSemantics { text = AnnotatedString(label) },
        horizontalArrangement = Arrangement.spacedBy(TagSpacing),
        verticalArrangement = Arrangement.spacedBy(TagSpacing),
    ) {
        tags.forEach { tag ->
            Text(
                tag,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}
