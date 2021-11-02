package de.tierwohlteam.android.futterapp.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.MainActivity
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
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
import java.io.IOException
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class AddRatingFragmentTest {

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

    @OptIn(ExperimentalTime::class)
    @Test
    fun clickFABInsertRatingTest() = runBlockingTest {
        val ratingText = "Sehr gut"
        val ratingStars = 3F // TODO The default rating is 3, don't know how to set this via espresso
        onView(withId(R.id.ti_rating)).perform(replaceText(ratingText))
        onView(withId(R.id.fab_addRating)).perform(click())
        repository.allRatings.test {
            val loading = awaitItem()
            assertThat(loading.status).isEqualTo(Status.LOADING)
            val repResult = awaitItem()
            assertThat(repResult.status).isEqualTo(Status.SUCCESS)
            assertThat(repResult.data).hasSize(1)
            assertThat(repResult.data!!.first().comment).isEqualTo(ratingText)
            assertThat(repResult.data!!.first().value).isEqualTo(ratingStars)
            cancelAndIgnoreRemainingEvents()
        }
    }

}