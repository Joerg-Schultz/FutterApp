package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.room.Dao
import androidx.room.Insert
import de.tierwohlteam.android.futterapp.models.Rating

@Dao
interface RatingDao {

    @Insert
    suspend fun insert(rating: Rating)
}