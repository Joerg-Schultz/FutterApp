package de.tierwohlteam.android.futterapp.viewModels

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.getOrAwaitValueTest
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.others.Event
import de.tierwohlteam.android.futterapp.others.Resource
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class RatingViewModelTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var repository: FutterAppRepository

    private lateinit var viewModel: RatingViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = RatingViewModel(repository)
    }

    @Test
    fun insertRating() {
        runBlockingTest {
            viewModel.insertRating(value = 3, comment = "gut")
        }
        Log.d("RATINGS", "inserted Rating")
        val value = viewModel.insertRatingStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }


    @Test
    fun insertNegativeRating() {
        runBlockingTest {
            viewModel.insertRating(value = -3, comment = "gut")
            val value = viewModel.insertRatingStatus.getOrAwaitValueTest()
            assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
        }
    }

    //https://fabiosanto.medium.com/unit-testing-coroutines-state-flow-c6e6de580027
    //https://github.com/cashapp/turbine
    //https://stackoverflow.com/questions/62110761/unit-test-the-new-kotlin-coroutine-stateflow
    @OptIn(ExperimentalTime::class)
    @Test
    fun getAllRatings() = runBlockingTest {
        viewModel.insertRating(value = 3, comment = "gut")
        viewModel.insertRating(value = 5, comment = "sehr gut")

        viewModel.allRatings.test {
            viewModel.getAllRatings()
            assertThat(expectItem().getContentIfNotHandled()?.status).isEqualTo(Status.EMPTY)
            assertThat(expectItem().getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
        }

    }
}