package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
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
class RatingDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var db: FutterAppDB

    lateinit var ratingDao: RatingDao
    @Before
    internal fun setup() {
        hiltRule.inject()
        ratingDao = db.ratingDao()
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetRating() = runBlockingTest {
        val ratingID = uuid4()
        val rating = Rating(id = ratingID, value = 3, comment = "reasonable")
        ratingDao.insert(rating)
        val dbRating = ratingDao.getByID(ratingID)
        assertThat(dbRating).isNotNull()
        assertThat(dbRating).isEqualTo(rating)
    }

    @Test
    @Throws(Exception::class)
    fun getAllRating() = runBlockingTest {
        val rating1 = Rating(value = 3, comment = "reasonable")
        val rating2 = Rating(value = 5, comment = "excellent")
        ratingDao.insert(rating1)
        ratingDao.insert(rating2)
        val ratingList = ratingDao.getAll().first()
        assertThat(ratingList).isNotEmpty()
        assertThat(ratingList).contains(rating1)
        assertThat(ratingList).contains(rating2)
    }
}