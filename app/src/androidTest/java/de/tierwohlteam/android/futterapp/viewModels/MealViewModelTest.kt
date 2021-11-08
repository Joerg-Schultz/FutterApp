package de.tierwohlteam.android.futterapp.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.others.Status
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

    @Test
    fun allMealsTest() = runBlockingTest {
        val mealViewModel = MealViewModel(repository)
        mealViewModel.allMeals.test {
            val meals = awaitItem()
            assertThat(meals.status).isEqualTo(Status.SUCCESS)
            assertThat(meals.data).isEmpty()
        }
    }

    @Test
    fun saveMealTest() = runBlockingTest {
        val mealViewModel = MealViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 250
        mealViewModel.addIngredient(foodType, foodName, gram)
        mealViewModel.saveMeal()
        mealViewModel.allMeals.test {
            val meals = awaitItem()
            assertThat(meals.status).isEqualTo(Status.SUCCESS)
            assertThat(meals.data).hasSize(1)
            val dbMeal = meals.data!!.first()
            assertThat(dbMeal.ingredients).hasSize(1)
            assertThat(dbMeal.ingredients.first().gram).isEqualTo(gram)
        }
    }

    @Test
    fun insertMealFlowTest() = runBlockingTest {
        val mealViewModel = MealViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 250
        mealViewModel.addIngredient(foodType, foodName, gram)

        mealViewModel.insertMealFlow.test {
            mealViewModel.saveMeal()
            val resourceLoading = awaitItem()
            assertThat(resourceLoading.status).isEqualTo(Status.LOADING)
            val resource = awaitItem()
            assertThat(resource.status).isEqualTo(Status.SUCCESS)
            assertThat(resource.data!!.ingredients).hasSize(1)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun latestMealTest() = runBlockingTest {
        val mealViewModel = MealViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 250
        mealViewModel.addIngredient(foodType, foodName, gram)
        val secondGram = 500
        mealViewModel.addIngredient(foodType, foodName, secondGram)
        mealViewModel.saveMeal()
        mealViewModel.ingredientList.test {
            val meal = awaitItem()
            assertThat(meal).hasSize(2)
            assertThat(meal[1].gram).isEqualTo(secondGram)
        }
    }

    @Test
    fun allFoodsTest() = runBlockingTest {
        val mealViewModel = MealViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 250
        mealViewModel.addIngredient(foodType, foodName, gram)
        mealViewModel.saveMeal()
        mealViewModel.allFoods.test {
            val resource = awaitItem()
            assertThat(resource.data).isNotNull()
            val foodList = resource.data!!
            assertThat(foodList).hasSize(1)
            assertThat(foodList.first().group).isEqualTo(foodType)
        }
    }
}
