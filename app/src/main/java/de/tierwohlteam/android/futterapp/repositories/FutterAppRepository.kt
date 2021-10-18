package de.tierwohlteam.android.futterapp.repositories

import android.util.Log
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.futterapp.models.*
import de.tierwohlteam.android.futterapp.others.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
     * get all Ratings as Resource Flow
     */
    val allRatings: Flow<Resource<List<Rating>>> = flow {
        emit(Resource.loading(null))
        val dataFlow = ratingDao.getAll()
        emitAll(dataFlow.map { Resource.success(it) })
    }
    /**
     * Food functions
     */
    private val foodDao = database.foodDao()

    /**
     * get all foods
     * @return Flow of Resource of list of food
     */
    val allFoods: Flow<Resource<List<Food>>> = flow {
        emit(Resource.loading(null))
        val dataFlow = foodDao.getAll()
        emitAll(dataFlow.map { Resource.success(it) })
    }

    /**
     * get a food by its ID
     * @param[id] Uuid
     * @return Food
     */
    suspend fun getFoodByID(id: Uuid): Food = foodDao.getByID(id)
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
     * get all Meals
     */
    val allMeals: Flow<Resource<List<Meal>>> = flow {
        emit(Resource.loading(null))
        val dataFlow = mealDao.getAll()
        emitAll(dataFlow.map { Resource.success(it) })
    }

    /**
     * get all Meals
     */
    val latestMeal: Flow<Resource<Meal>> = flow {
        emit(Resource.loading(null))
        val dataFlow = mealDao.getLatest()
        emitAll(dataFlow.map { Resource.success(it) })
    }

    /**
     * Fridge functions
     */
    private val fridgeDao = database.fridgeDao()

    /**
     * get the current content
     */
    val fridgeContent: Flow<Resource<List<PacksInFridge>>> = flow {
        emit(Resource.loading(null))
        val dataFlow = fridgeDao.content()
        emitAll(dataFlow.map { Resource.success(it) })
    }

    /**
     * add a pack to the fridge
     * @param[pack] Pack
     * @return PacksInFridge with updated count for this pack
     */
    suspend fun addPacksToFridge(pack: Pack, amount: Int = 0): PacksInFridge =
        fridgeDao.addPacks(pack, amount)

    /**
     * get a pack from the fridge
     * @param[pack]
     * @return PacksInFridge with updated count for this pack
     *          or null if there was no more pack in fridge
     */
    suspend fun getPackFromFridge(pack: Pack): PacksInFridge? =
        fridgeDao.getPack(pack)

}