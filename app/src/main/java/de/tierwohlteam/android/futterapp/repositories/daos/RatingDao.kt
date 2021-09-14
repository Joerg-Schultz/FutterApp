package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.futterapp.models.Rating

@Dao
interface RatingDao {

    @Insert
    suspend fun insert(rating: Rating)

    @Query("SELECT * from rating where id = :ratingID")
    suspend fun getByID(ratingID: Uuid): Rating?
}