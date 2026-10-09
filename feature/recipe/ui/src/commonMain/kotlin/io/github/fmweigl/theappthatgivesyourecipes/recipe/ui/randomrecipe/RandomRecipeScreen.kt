package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.randomrecipe

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.ContentFadeSpec
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.ErrorMessage
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.MaxColumnWidth
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.RecipeDetails
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.RecipeSkeleton
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.randomrecipe.RandomRecipeUiState.Content
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.Res
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.next_recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.previous_recipe
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RandomRecipeScreen(
    uiState: RandomRecipeUiState,
    onShowNext: () -> Unit,
    onShowPrevious: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Fades between skeleton, error and recipes, also from one recipe to the next. Each recipe
        // is its own content, so it starts scrolled to the top, and the one fading out keeps its
        // state (its heart) from before.
        AnimatedContent(
            targetState = uiState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            transitionSpec = { fadeIn(ContentFadeSpec) togetherWith fadeOut(ContentFadeSpec) },
            contentKey = { it.content.key },
        ) { shown ->
            when (val content = shown.content) {
                Content.Loading -> RecipeSkeleton()
                is Content.Error -> ErrorMessage(content.error, onRetry = onShowNext)
                is Content.Success -> RecipeDetails(
                    recipe = content.recipe,
                    isFavorite = shown.isFavorite,
                    onToggleFavorite = onToggleFavorite,
                )
            }
        }
        HorizontalDivider()
        RecipeNavigationBar(
            canShowPrevious = uiState.canShowPrevious,
            canShowNext = uiState.content !is Content.Loading,
            onShowPrevious = onShowPrevious,
            onShowNext = onShowNext,
        )
    }
}

/** What the crossfade tells apart: the skeleton, an error, or one particular recipe. */
private val Content.key: Any
    get() = when (this) {
        Content.Loading, is Content.Error -> this
        is Content.Success -> recipe.id
    }

@Composable
private fun RecipeNavigationBar(
    canShowPrevious: Boolean,
    canShowNext: Boolean,
    onShowPrevious: () -> Unit,
    onShowNext: () -> Unit,
) {
    Row(
        // As wide as the recipe's column (padding outside the limit, as there), centered.
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .widthIn(max = MaxColumnWidth)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = onShowPrevious,
            enabled = canShowPrevious,
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(stringResource(Res.string.previous_recipe))
        }
        Button(
            onClick = onShowNext,
            enabled = canShowNext,
            modifier = Modifier.weight(1f),
        ) {
            Text(stringResource(Res.string.next_recipe))
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
        }
    }
}
