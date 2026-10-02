package io.github.fmweigl.yetanothermealsapp.recipe.data.database

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoriteRecipeDao {

    @Upsert
    suspend fun upsert(recipe: FavoriteRecipeEntity)

    @Query("DELETE FROM favorite_recipe WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM favorite_recipe WHERE id = :id")
    suspend fun getById(id: String): FavoriteRecipeEntity?

    @Query("SELECT * FROM favorite_recipe ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<FavoriteRecipeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_recipe WHERE id = :id)")
    fun observeExists(id: String): Flow<Boolean>
}
