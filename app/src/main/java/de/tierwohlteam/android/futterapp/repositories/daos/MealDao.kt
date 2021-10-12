package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import de.tierwohlteam.android.futterapp.models.Feeding
import de.tierwohlteam.android.futterapp.models.Ingredient
import de.tierwohlteam.android.futterapp.models.Meal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Dao
interface MealDao {

    @Insert
    suspend fun insertFeeding(feeding: Feeding)
    @Insert
    suspend fun insertIngredient(ingredient: Ingredient)

    suspend fun insert(meal: Meal) {
        GlobalScope.launch {
            insertFeeding(meal.feeding)
            meal.ingredients.forEach { insertIngredient(it) }
        }
    }

    @Query("SELECT * from feeding")
    fun getAll(): Flow<List<Meal>>
}