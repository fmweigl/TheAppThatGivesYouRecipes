package io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model

import kotlin.time.Instant

/**
 * A favorite that was just removed: the [recipe] and when it was [savedAt], so that
 * `FavoritesRepository.restoreFavorite` can put it back at its old position.
 */
data class RemovedFavorite(val recipe: Recipe, val savedAt: Instant)
