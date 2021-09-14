package de.tierwohlteam.android.futterapp.repositories

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
}