package com.example.yetanothermealsapp.randomrecipe.domain

data class Recipe(
    val id: String,
    val name: String,
    val category: String? = null,
    val area: String? = null,
    val instructions: String? = null,
    val imageUrl: String? = null,
    val youtubeUrl: String? = null,
    val sourceUrl: String? = null,
    val tags: List<String> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
)
