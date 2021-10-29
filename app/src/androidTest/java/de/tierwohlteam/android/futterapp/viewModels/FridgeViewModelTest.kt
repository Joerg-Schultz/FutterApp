package de.tierwohlteam.android.futterapp.viewModels

import android.util.Log
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
class FridgeViewModelTest {
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
    fun emptyFridgeTest() = runBlockingTest {
        val fridgeViewModel = FridgeViewModel(repository)
        fridgeViewModel.content.test {
            val content = awaitItem()
            assertThat(content.status).isEqualTo(Status.SUCCESS)
            assertThat(content.data).isNotNull()
            assertThat(content.data).isEmpty()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun addToFridgeInsertFlowTest() = runBlockingTest {
        val fridgeViewModel = FridgeViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 500
        val amount = 2
        fridgeViewModel.addToFridge(foodType, foodName, gram, amount)
        fridgeViewModel.insertPacksFlow.test {
            val insertResult = awaitItem()
            val resource = insertResult.getContentIfNotHandled()!!
            assertThat(resource.status).isEqualTo(Status.SUCCESS)
            assertThat(resource.data).isNotNull()
            assertThat(resource.data!!.amount).isEqualTo(amount)
            assertThat(resource.data!!.pack.size).isEqualTo(gram)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun addToFridgeContentTest() = runBlockingTest {
        val fridgeViewModel = FridgeViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 500
        val amount = 2
        fridgeViewModel.addToFridge(foodType, foodName, gram, amount)
        fridgeViewModel.content.test {
            val content = awaitItem()
            assertThat(content.status).isEqualTo(Status.SUCCESS)
            assertThat(content.data).isNotNull()
            assertThat(content.data).isNotEmpty()
            val packs = content.data!!
            assertThat(packs).hasSize(1)
            assertThat(packs.first().amount).isEqualTo(amount)
            assertThat(packs.first().pack.food.name).isEqualTo(foodName)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun deleteFromFridgeContentTest() = runBlockingTest {
        val fridgeViewModel = FridgeViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 500
        val amount = 1
        fridgeViewModel.addToFridge(foodType, foodName, gram, amount)
        fridgeViewModel.content.test {
            val content = awaitItem()
            assertThat(content.status).isEqualTo(Status.SUCCESS)
            assertThat(content.data).isNotNull()
            assertThat(content.data).isNotEmpty()
            fridgeViewModel.deleteOnePack(0)
            val loading = awaitItem()
            assertThat(loading.status).isEqualTo(Status.LOADING)
            val empty = awaitItem()
            assertThat(empty.status).isEqualTo(Status.SUCCESS)
            assertThat(empty.data).isNotNull()
            assertThat(empty.data).isEmpty()
            cancelAndConsumeRemainingEvents()
        }
    }
    @Test
    fun deleteFromFridgeContentTest2() = runBlockingTest {
        val fridgeViewModel = FridgeViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 500
        val amount = 2
        fridgeViewModel.addToFridge(foodType, foodName, gram, amount)
        fridgeViewModel.content.test {
            val content = awaitItem()
            assertThat(content.status).isEqualTo(Status.SUCCESS)
            assertThat(content.data).isNotNull()
            assertThat(content.data).isNotEmpty()
            fridgeViewModel.deleteOnePack(0)
            val loading = awaitItem()
            assertThat(loading.status).isEqualTo(Status.LOADING)
            val oneOut = awaitItem()
            assertThat(oneOut.status).isEqualTo(Status.SUCCESS)
            assertThat(oneOut.data).isNotNull()
            assertThat(oneOut.data).isNotEmpty()
            assertThat(oneOut.data!!.first().amount).isEqualTo(1)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun contentWithEmptyTest() = runBlockingTest {
        val fridgeViewModel = FridgeViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 500
        val amount = 0
        fridgeViewModel.addToFridge(foodType, foodName, gram, amount)
        fridgeViewModel.contentWithEmpty.test {
            val content = awaitItem()
            assertThat(content.status).isEqualTo(Status.SUCCESS)
            assertThat(content.data).isNotNull()
            assertThat(content.data).isNotEmpty()
            assertThat(content.data!!.first().amount).isEqualTo(0)
            cancelAndConsumeRemainingEvents()
        }
    }
}