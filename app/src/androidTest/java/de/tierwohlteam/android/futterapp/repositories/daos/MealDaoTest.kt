package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.models.Meal
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class MealDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("testDB")
    lateinit var db: FutterAppDB
    @Inject
    @Named("testMealDao")
    lateinit var mealDao: MealDao

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
    fun insertAndGetMeal() = runBlockingTest {
        val meal = Meal()
        val meat = Food(FoodType.MEAT, "Rindermuskel")
        val meatGrams = 250
        val carbs = Food(FoodType.CARBS, "Buchweizen")
        val carbGrams = 50
        meal.addIngredient(meat, meatGrams)
        meal.addIngredient(carbs, carbGrams)
        mealDao.insert(meal)
        assertThat(carbGrams).isEqualTo(50)
    }
}