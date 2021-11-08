package de.tierwohlteam.android.futterapp.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.MainActivity
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.adapters.FridgeListAdapter
import de.tierwohlteam.android.futterapp.launchFragmentInHiltContainer
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import de.tierwohlteam.android.futterapp.viewModels.FridgeViewModel
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
import java.io.IOException
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
@ExperimentalTime
class ShowFridgeFragmentTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //injected ony to close db after end of tests
    @Inject
    lateinit var db: FutterAppDB
    @Inject
    lateinit var repository: FutterAppRepository

    private val dispatcher = TestCoroutineDispatcher()
    @Before
    fun setup() {
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
    fun swipeRightToDeletePackTest() = runBlockingTest {
        val fridgeViewModel = FridgeViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 250
        val amount = 1
        fridgeViewModel.addToFridge(foodType, foodName, gram, amount)
        launchFragmentInHiltContainer<ShowFridgeFragment> {
        }
        onView(withId(R.id.rv_showfridge)).perform(
            RecyclerViewActions.actionOnItemAtPosition<FridgeListAdapter.FridgeViewHolder>(
                0,
                swipeRight()
            )
        )
        fridgeViewModel.content.test {
            val resource = awaitItem()
            assertThat(resource.status).isEqualTo(Status.SUCCESS)
            assertThat(resource.data).isEmpty()
        }
    }

    @Test
    fun swipeRightToDeletePackWithEmptyTest() = runBlockingTest {
        val fridgeViewModel = FridgeViewModel(repository)
        val foodType = FoodType.MEAT
        val foodName = "Pute"
        val gram = 250
        val amount = 1
        fridgeViewModel.addToFridge(foodType, foodName, gram, amount)
        launchFragmentInHiltContainer<ShowFridgeFragment> {
        }
        onView(withId(R.id.rv_showfridge)).perform(
            RecyclerViewActions.actionOnItemAtPosition<FridgeListAdapter.FridgeViewHolder>(
                0,
                swipeRight()
            )
        )
        fridgeViewModel.contentWithEmpty.test {
            val resource = awaitItem()
            assertThat(resource.status).isEqualTo(Status.SUCCESS)
            assertThat(resource.data).hasSize(1)
            val pack = resource.data?.get(0)!!
            assertThat(pack.amount).isEqualTo(0)
        }
    }
}