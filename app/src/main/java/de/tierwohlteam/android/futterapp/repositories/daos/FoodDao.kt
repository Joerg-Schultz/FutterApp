package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.tierwohlteam.android.futterapp.models.Food

@Dao
interface FoodDao {

    @Insert
    suspend fun insert(food: Food)

    @Query("SELECT * from food where name = :name")
    fun getByName(name: String): Food?
}