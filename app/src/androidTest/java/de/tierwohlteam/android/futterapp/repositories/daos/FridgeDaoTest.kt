package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.PacksInFridge
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Inject


@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class FridgeDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var db: FutterAppDB

    lateinit var fridgeDao: FridgeDao
    @Before
    internal fun setup() {
        hiltRule.inject()
        fridgeDao = db.fridgeDao()
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun content() = runBlockingTest {
        val content: List<PacksInFridge> = fridgeDao.content()
        assertThat(content).isEmpty()
    }
}