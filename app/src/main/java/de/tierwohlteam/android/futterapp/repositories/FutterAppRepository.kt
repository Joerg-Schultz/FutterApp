package de.tierwohlteam.android.futterapp.repositories

import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.futterapp.models.*
import de.tierwohlteam.android.futterapp.repositories.daos.FoodDao
import de.tierwohlteam.android.futterapp.repositories.daos.MealDao
import de.tierwohlteam.android.futterapp.repositories.daos.RatingDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FutterAppRepository @Inject constructor(
    private val database: FutterAppDB
) {
    /**
     * Rating functions
     */
    private val ratingDao = database.ratingDao()
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
     * get all Ratings as Flow
     */
    val allRatings: Flow<List<Rating>>
        get() = ratingDao.getAll()

    /**
     * Food functions
     */
    private val foodDao = database.foodDao()
    /**
     * Insert a Food
     * make sure, that the id is correct!
     * @param[food] a new Food
     */
    suspend fun insertFood(food: Food) = foodDao.insert(food)

    /**
     * get a food and insert it if not yet in DB
     * @param[type] FoodType
     * @param[name] name of the food
     * @return[Food]
     */
    suspend fun getFoodByNameAndType(type: FoodType, name: String): Food = foodDao.getAndInsert(group = type, name = name)


    /**
     * get list of food by its Name
     * returns list as carrots can be cooked and raw
     * @param[foodName] string
     * @return List of Food
     */
    suspend fun getFoodByName(foodName: String): List<Food> = foodDao.getByName(foodName)

    /**
     * Meal function
     */
    private val mealDao = database.mealDao()
    /**
     * Insert a Meal
     */
    suspend fun insertMeal(meal: Meal) = mealDao.insert(meal)

    /**
     * Fridge functions
     */
    private val fridgeDao = database.fridgeDao()
    /**
     * get the current content
     */
    suspend fun fridgeContent() = fridgeDao.content()

    /**
     * add a pack to the fridge
     * @param[pack] Pack
     * @return PacksInFridge with updated count for this pack
     */
    suspend fun addPackToFridge(pack: Pack): PacksInFridge =
        fridgeDao.addPack(pack)

    /**
     * get a pack from the fridge
     * @param[pack]
     * @return PacksInFridge with updated count for this pack
     *          or null if there was no more pack in fridge
     */
    suspend fun getPackFromFridge(pack: Pack): PacksInFridge? =
        fridgeDao.getPack(pack)
}