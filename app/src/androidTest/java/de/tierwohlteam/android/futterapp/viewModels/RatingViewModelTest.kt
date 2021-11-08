package de.tierwohlteam.android.futterapp.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.others.Resource
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
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
class RatingViewModelTest {
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
    fun insertRatingTest() = runBlockingTest {
        val ratingViewModel = RatingViewModel(repository)
        val rating = 3.5F
        val comment = "Excellent"

        ratingViewModel.insertRatingFlow.test {
            ratingViewModel.insertRating(rating, comment)
            val resource = awaitItem()
            assertThat(resource.status).isEqualTo(Status.SUCCESS)
            assertThat(resource.data!!.value).isEqualTo(rating)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getAllRatingsTest() = runBlockingTest {
        val ratingViewModel = RatingViewModel(repository)
        val rating = 3.5F
        val comment = "Excellent"
        val insertJob = launch {
            ratingViewModel.insertRating(rating, comment)
        }
        insertJob.join()

        ratingViewModel.allRatings.test {
            val resource = awaitItem()
            assertThat(resource.status).isEqualTo(Status.SUCCESS)
            assertThat(resource.data).hasSize(1)
            assertThat(resource.data?.get(0)?.value).isEqualTo(rating)
        }

    }
}