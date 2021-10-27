package de.tierwohlteam.android.futterapp.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.*
import de.tierwohlteam.android.futterapp.others.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import org.junit.Before
import org.junit.Rule
import java.io.IOException
import javax.inject.Inject
import kotlin.time.ExperimentalTime

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

    private val dispatcher = TestCoroutineDispatcher()
    @Before
    internal fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(dispatcher)
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        Dispatchers.resetMain()
    }

    @Test
    fun insertAndGetRating() = runBlockingTest {
        val ratingID = uuid4()
        val rating = Rating(id = ratingID, value = 3F, comment = "reasonable")
        repository.insertRating(rating)
        val dbRating = repository.getRatingByID(ratingID)
        assertThat(dbRating).isNotNull()
        assertThat(dbRating).isEqualTo(rating)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun getAllRatingsTest() = runBlockingTest {
        val rating1 = Rating(value = 3F, comment = "reasonable")
        val rating2 = Rating(value = 5F, comment = "excellent")
        val job = launch {
            repository.insertRating(rating1)
            repository.insertRating(rating2)
        }
        job.join()
        repository.allRatings.test {
            val loading = awaitItem()
            assertThat(loading.status).isEqualTo(Status.LOADING)
            val repResult = awaitItem()
            assertThat(repResult.status).isEqualTo(Status.SUCCESS)
            assertThat(repResult.data).hasSize(2)
            assertThat(repResult.data).containsExactly(rating1, rating2)
            cancelAndIgnoreRemainingEvents()
        }
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

    @OptIn(ExperimentalTime::class)
    @Test
    fun fridgeContent() = runBlockingTest {
        repository.fridgeContent.test {
            val resourceBefore = awaitItem()
            assertThat(resourceBefore.status).isEqualTo(Status.LOADING)
            val resourceAfter = awaitItem()
            assertThat(resourceAfter.status).isEqualTo(Status.SUCCESS)
            assertThat(resourceAfter.data).isEmpty()
        }
    }
/*
    @Test
    fun addPackToFridge() = runBlockingTest {
        val food = Food(group = FoodType.MEAT, name = "RinderMuskel")
        val pack = Pack(food = food, size = 500)
        launch {
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
        launch {
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

 */
}