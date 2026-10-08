package io.github.fmweigl.yetanothermealsapp.favorites.ui

/**
 * [removed] is the favorite just removed, offered for "Undo" until the message closes;
 * [restoreFailed] is one whose "Undo" failed, reported until that message closes. [removing] holds
 * the ids of favorites being removed (their hearts are shown empty), and [removeFailed] is one that
 * couldn't be removed, reported until that message closes.
 */
internal data class FavoritesUiState(
    val content: Content = Content.Loading,
    val removed: RecipeTeaser? = null,
    val restoreFailed: RecipeTeaser? = null,
    val removing: Set<String> = emptySet(),
    val removeFailed: RecipeTeaser? = null,
) {
    sealed interface Content {
        data object Loading : Content
        data object Empty : Content

        /** The favorites couldn't be read from the database. */
        data object Error : Content
        data class Favorites(val teasers: List<RecipeTeaser>) : Content
    }
}

/** What the list shows of a favorite; [subtitle] is "Category · Area", null if both are unknown. */
internal data class RecipeTeaser(
    val id: String,
    val name: String,
    val subtitle: String?,
    val imageUrl: String?,
)
