package de.tierwohlteam.android.futterapp.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.models.Meal
import de.tierwohlteam.android.futterapp.models.Rating
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

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

}