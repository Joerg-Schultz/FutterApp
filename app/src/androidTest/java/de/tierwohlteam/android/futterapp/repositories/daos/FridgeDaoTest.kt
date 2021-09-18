package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.models.Pack
import de.tierwohlteam.android.futterapp.models.PacksInFridge
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var repository: FutterAppRepository

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

    @Test
    fun addPack() = runBlockingTest {
        val food = Food(group = FoodType.MEAT, name = "RinderMuskel")
        val pack = Pack(food = food, size = 500)
        GlobalScope.launch {
            // food has to be inserted before pack (foreign key)
            repository.insertFood(food)
            fridgeDao.addPack(pack)
            val content = fridgeDao.content()
            assertThat(content).contains(PacksInFridge(pack, 1))
        }
    }

    @Test
    fun getPack() = runBlockingTest {
        val food = Food(group = FoodType.MEAT, name = "RinderMuskel")
        val pack = Pack(food = food, size = 500)
        GlobalScope.launch {
            repository.insertFood(food)
            fridgeDao.addPack(pack)
            val content = fridgeDao.content()
            assertThat(content).contains(PacksInFridge(pack, 1))
            val state = fridgeDao.getPack(pack)
            assertThat(state).isNotNull()
            assertThat(state!!.amount).isEqualTo(0)
            assertThat(fridgeDao.content()).contains(PacksInFridge(pack, 0))
        }
    }
}