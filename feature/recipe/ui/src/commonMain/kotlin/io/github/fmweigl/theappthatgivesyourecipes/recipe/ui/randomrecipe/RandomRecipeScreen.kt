package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.randomrecipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.ErrorMessage
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.LoadingIndicator
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.MaxColumnWidth
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.RecipeDetails
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
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (val content = uiState.content) {
                Content.Loading -> LoadingIndicator()
                is Content.Error -> ErrorMessage(content.error, onRetry = onShowNext)
                // A new list state per recipe, so each one starts scrolled to the top.
                is Content.Success -> key(content.recipe.id) {
                    RecipeDetails(
                        recipe = content.recipe,
                        isFavorite = uiState.isFavorite,
                        onToggleFavorite = onToggleFavorite,
                    )
                }
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
