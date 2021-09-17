package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.benasher44.uuid.uuid4
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
    lateinit var db: FutterAppDB


    lateinit var foodDao: FoodDao
    @Before
    internal fun setup() {
        hiltRule.inject()
        foodDao = db.foodDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getByName() = runBlockingTest {
        val group = FoodType.VEGGIES_COOKED
        val type = "carrot"
        val id = uuid4()
        val carrot = Food(id = id, name = type, group = group)
        foodDao.insert(carrot)
        val dbCarrots = foodDao.getByName("carrot")
        assertThat(dbCarrots).isNotEmpty()
        assertThat(dbCarrots).isEqualTo(listOf(carrot))
    }

    @Test
    @Throws(Exception::class)
    fun getByNameAndGroup() = runBlockingTest {
        val group = FoodType.VEGGIES_COOKED
        val name = "carrot"
        val carrot = foodDao.getAndInsert(name = name, group = group)
        assertThat(carrot).isNotNull()
        assertThat(carrot.name).isEqualTo(name)
        assertThat(carrot.group).isEqualTo(group)
    }

}
