package de.tierwohlteam.android.futterapp.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@SmallTest
@HiltAndroidTest
class MealViewModelTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var repository: FutterAppRepository

    // https://youtu.be/uEnpIFVspMo?t=1822
    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addIngredientTest() = runBlockingTest {
        val mealViewModel = MealViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 250
        mealViewModel.addIngredient(foodType, foodName, gram)

        mealViewModel.ingredientList.test {
            val componentList = awaitItem()
            assertThat(componentList).hasSize(1)
            assertThat(componentList.first().foodID).isNull()
            assertThat(componentList.first().foodGroup).isEqualTo(foodType)
            assertThat(componentList.first().foodName).isEqualTo(foodName)
            assertThat(componentList.first().gram).isEqualTo(gram)
        }
    }

    @Test
    fun deleteIngredientTest() = runBlockingTest {
        val mealViewModel = MealViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 250
        mealViewModel.addIngredient(foodType, foodName, gram)
        mealViewModel.deleteIngredient(0)
        mealViewModel.ingredientList.test {
            val componentList = awaitItem()
            assertThat(componentList).isEmpty()
        }
    }

    @Test
    fun emptyIngredientTest() = runBlockingTest {
        val mealViewModel = MealViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 250
        mealViewModel.addIngredient(foodType, foodName, gram)
        mealViewModel.emptyIngredientList()
        mealViewModel.ingredientList.test {
            val componentList = awaitItem()
            assertThat(componentList).isEmpty()
        }
    }
}
