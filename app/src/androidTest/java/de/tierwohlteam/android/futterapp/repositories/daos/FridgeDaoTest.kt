package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.models.Pack
import de.tierwohlteam.android.futterapp.models.PacksInFridge
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var repository: FutterAppRepository

    private val dispatcher = TestCoroutineDispatcher()
    lateinit var fridgeDao: FridgeDao
    @Before
    internal fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(dispatcher)
        fridgeDao = db.fridgeDao()
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        Dispatchers.resetMain()
    }


    @OptIn(ExperimentalTime::class)
    @Test
    fun contentEmpty() = runBlockingTest {
        val contentFlow: Flow<List<PacksInFridge>> = fridgeDao.content()
        contentFlow.test {
            val content = awaitItem()
            assertThat(content).isEmpty()
        }
    }

    @Test
    fun addPack() = runBlockingTest {
        val food = Food(group = FoodType.MEAT, name = "RinderMuskel")
        val size = 500
        val pack = Pack(food = food, size = size)
        val amount = 2
        // food has to be inserted before pack (foreign key)
        repository.insertFood(food)
        val state = fridgeDao.addPacks(pack, amount)
        assertThat(state.amount).isEqualTo(amount)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun addPackContent() = runBlockingTest {
        val food = Food(group = FoodType.MEAT, name = "RinderMuskel")
        val size = 500
        val pack = Pack(food = food, size = size)
        val amount = 2
        // food has to be inserted before pack (foreign key)
        repository.insertFood(food)
        fridgeDao.addPacks(pack, amount)
        val content = fridgeDao.content()
        content.test {
            val currentPacks = awaitItem()
            assertThat(currentPacks).hasSize(1)
            assertThat(currentPacks.first().amount).isEqualTo(amount)
        }
    }

    @Test
    fun getPack() = runBlockingTest {
        val food = Food(group = FoodType.MEAT, name = "RinderMuskel")
        val size = 500
        val pack = Pack(food = food, size = size)
        repository.insertFood(food)
        fridgeDao.addPacks(pack)
        val state = fridgeDao.getPack(pack)
        assertThat(state).isNotNull()
        assertThat(state!!.amount).isEqualTo(0)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun getPackContent() = runBlockingTest {
        val food = Food(group = FoodType.MEAT, name = "RinderMuskel")
        val size = 500
        val pack = Pack(food = food, size = size)
        repository.insertFood(food)
        fridgeDao.addPacks(pack)
        val content = fridgeDao.content()
        content.test {
            val currentPacks = awaitItem()
            assertThat(currentPacks).hasSize(1)
            assertThat(currentPacks.first().amount).isEqualTo(1)
            fridgeDao.getPack(pack)
            val emptyFridge = awaitItem()
            assertThat(emptyFridge).isEmpty()
        }
    }
}