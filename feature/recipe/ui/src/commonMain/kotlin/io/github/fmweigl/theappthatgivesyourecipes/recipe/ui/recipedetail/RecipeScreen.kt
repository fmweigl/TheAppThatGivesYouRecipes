package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.recipedetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component.BackTopAppBar
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.ErrorMessage
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.LoadingIndicator
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.RecipeDetails
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.recipedetail.RecipeUiState.Content
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.Res
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.recipe
import org.jetbrains.compose.resources.stringResource

/**
 * A recipe opened from a list (such as the favorites). The top bar says "Recipe" rather than the
 * recipe's name, which the content shows as its heading right below; screen readers would read it twice.
 */
@Composable
internal fun RecipeScreen(
    uiState: RecipeUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        BackTopAppBar(title = stringResource(Res.string.recipe), onBack = onBack)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (val content = uiState.content) {
                Content.Loading -> LoadingIndicator()
                is Content.Error -> ErrorMessage(
                    content.error,
                    // Trying again can't bring back a recipe TheMealDB doesn't have.
                    onRetry = onRetry.takeUnless { content.error == DataError.NotFound },
                )
                is Content.Success -> RecipeDetails(
                    recipe = content.recipe,
                    isFavorite = uiState.isFavorite,
                    onToggleFavorite = onToggleFavorite,
                )
            }
        }
    }
}
