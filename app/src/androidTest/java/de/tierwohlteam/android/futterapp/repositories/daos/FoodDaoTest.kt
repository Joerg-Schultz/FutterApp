package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class FoodDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("testDB")
    lateinit var db: FutterAppDB
    @Inject
    @Named("testFoodDao")
    lateinit var foodDao: FoodDao

    @Before
    internal fun setup() {
        hiltRule.inject()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetFood() = runBlockingTest {
        val group = FoodType.VEGGIES_COOKED
        val type = "carrot"
        val carrot = Food(name = type, group = group)
        foodDao.insert(carrot)
        val dbCarrot = foodDao.getByName("carrot")
        assertThat(dbCarrot).isNotNull()
        assertThat(dbCarrot).isEqualTo(carrot)
    }

}
