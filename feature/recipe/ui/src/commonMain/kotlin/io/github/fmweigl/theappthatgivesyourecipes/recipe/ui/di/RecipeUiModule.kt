package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.di

import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.randomrecipe.RandomRecipeViewModel
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.recipedetail.RecipeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val recipeUiModule = module {
    viewModelOf(::RandomRecipeViewModel)
    // The recipe id comes from the navigation key, through koinViewModel { parametersOf(recipeId) }.
    viewModel { params ->
        RecipeViewModel(recipeId = params.get(), recipeRepository = get(), favoritesRepository = get())
    }
}
