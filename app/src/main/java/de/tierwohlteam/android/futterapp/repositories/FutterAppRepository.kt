package de.tierwohlteam.android.futterapp.repositories

import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.repositories.daos.RatingDao
import javax.inject.Inject

class FutterAppRepository @Inject constructor(
    private val ratingDao: RatingDao,
) {
    /**
     * Rating functions
     */
    /**
     * Insert a rating
     * @param[rating] a new Rating
     */
    suspend fun insertRating(rating: Rating) = ratingDao.insert(rating)

    /**
     * get a rating by its ID
     * @param[ratingID] uuid of a rating
     */
    suspend fun getRatingByID(ratingID: Uuid): Rating? = ratingDao.getByID(ratingID)
}