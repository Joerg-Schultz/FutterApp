package de.tierwohlteam.android.futterapp.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.others.Resource
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

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

    @Before
    fun setup() {
        hiltRule.inject()
    }

    // TODO change LiveData to Flow
    // TODO then adapt test
    @Test
    fun insertRatingTest() = runBlockingTest {
        val ratingViewModel = RatingViewModel(repository)
        val rating = 3.5F
        val comment = "Excellent"
        val insertJob = launch {
            ratingViewModel.insertRating(rating, comment)
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

        ratingViewModel.getAllRatings()
        ratingViewModel.allRatings.collect { result ->
            if (result.status == Status.SUCCESS) {
                assertThat(result.status).isEqualTo(Status.LOADING)
            } else {
                assertThat(result.status).isEqualTo(Status.SUCCESS)
            }
        }

    }
}