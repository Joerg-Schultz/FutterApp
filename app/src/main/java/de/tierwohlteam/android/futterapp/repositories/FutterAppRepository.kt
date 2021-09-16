package de.tierwohlteam.android.futterapp.repositories

import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.Meal
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.repositories.daos.FoodDao
import de.tierwohlteam.android.futterapp.repositories.daos.MealDao
import de.tierwohlteam.android.futterapp.repositories.daos.RatingDao
import javax.inject.Inject

class FutterAppRepository @Inject constructor(
    private val ratingDao: RatingDao,
    private val foodDao: FoodDao,
    private val mealDao: MealDao
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


    /**
     * Food functions
     */
    /**
     * Insert a Food
     * @param[food] a new Food
     */
    suspend fun insertFood(food: Food) = foodDao.insert(food)

    /**
     * get a rating by its ID
     * @param[ratingID] uuid of a rating
     */
    suspend fun getFoodByName(foodName: String): Food? = foodDao.getByName(foodName)

    /**
     * Meal function
     */
    /**
     * Insert a Meal
     */
    suspend fun insertMeal(meal: Meal) = mealDao.insert(meal)
}