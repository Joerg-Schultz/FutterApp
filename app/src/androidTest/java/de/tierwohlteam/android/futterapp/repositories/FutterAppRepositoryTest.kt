package de.tierwohlteam.android.futterapp.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Test
import org.junit.Before
import org.junit.Rule
import java.io.IOException
import javax.inject.Inject

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class FutterAppRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var db: FutterAppDB
    @Inject
    lateinit var repository: FutterAppRepository

    @Before
    internal fun setup() {
        hiltRule.inject()
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetRating() = runBlockingTest {
        val ratingID = uuid4()
        val rating = Rating(id = ratingID, value = 3, comment = "reasonable")
        repository.insertRating(rating)
        val dbRating = repository.getRatingByID(ratingID)
        assertThat(dbRating).isNotNull()
        assertThat(dbRating).isEqualTo(rating)
    }

    @Test
    fun getAllRatings() = runBlockingTest {
        val rating1 = Rating(value = 3, comment = "reasonable")
        val rating2 = Rating(value = 5, comment = "excellent")
        repository.insertRating(rating1)
        repository.insertRating(rating2)
        val ratingList = repository.allRatings.first()
        assertThat(ratingList).isNotEmpty()
        assertThat(ratingList).contains(rating1)
        assertThat(ratingList).contains(rating2)
    }

    @Test
    fun insertAndGetFood() = runBlockingTest {
        val group = FoodType.VEGGIES_COOKED
        val type = "carrot"
        val carrot = Food(name = type, group = group)
        repository.insertFood(carrot)
        val dbCarrot = repository.getFoodByName("carrot")
        assertThat(dbCarrot).isNotEmpty()
        assertThat(dbCarrot).isEqualTo(listOf(carrot))
    }

    @Test
    fun insertMeal() = runBlockingTest {
        val meal = Meal()
        val meat = Food(FoodType.MEAT, "Rindermuskel")
        val meatGrams = 250
        val carbs = Food(FoodType.CARBS, "Buchweizen")
        val carbGrams = 50
        meal.addIngredient(meat, meatGrams)
        meal.addIngredient(carbs, carbGrams)
        repository.insertMeal(meal)
        assertThat(carbGrams).isEqualTo(50)
    }

    @Test
    fun fridgeContent() = runBlockingTest {
        val content: List<PacksInFridge> = repository.fridgeContent()
        assertThat(content).isEmpty()
    }

    @Test
    fun addPackToFridge() = runBlockingTest {
        val food = Food(group = FoodType.MEAT, name = "RinderMuskel")
        val pack = Pack(food = food, size = 500)
        GlobalScope.launch {
            // food has to be inserted before pack (foreign key)
            repository.insertFood(food)
            val state = repository.addPackToFridge(pack)
            assertThat(state).isNotNull()
            assertThat(state.amount).isEqualTo(1)
            val content = repository.fridgeContent()
            assertThat(content).contains(PacksInFridge(pack, 1))
        }
    }

    @Test
    fun getPackFromFridge() = runBlockingTest {
        val food = Food(group = FoodType.MEAT, name = "RinderMuskel")
        val pack = Pack(food = food, size = 500)
        GlobalScope.launch {
            repository.insertFood(food)
            repository.addPackToFridge(pack)
            val content = repository.fridgeContent()
            assertThat(content).contains(PacksInFridge(pack, 1))
            val state = repository.getPackFromFridge(pack)
            assertThat(state).isNotNull()
            assertThat(state!!.amount).isEqualTo(0)
            assertThat(repository.fridgeContent()).contains(PacksInFridge(pack, 0))
        }
    }
}