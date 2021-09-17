package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.FoodType

@Dao
interface FoodDao {

    @Insert
    suspend fun insert(food: Food)

    @Query("SELECT * from food where name = :name")
    suspend fun getByName(name: String): List<Food>

    @Query("SELECT * from food where name = :name and \"group\" = :group")
    suspend fun getByNameAndType(group: FoodType, name: String): Food?

    suspend fun getAndInsert(group: FoodType, name: String): Food {
        var dbFood = getByNameAndType(group, name)
        if (dbFood == null) {
            dbFood = Food(group = group, name = name)
            insert(dbFood)
        }
        return dbFood
    }
}