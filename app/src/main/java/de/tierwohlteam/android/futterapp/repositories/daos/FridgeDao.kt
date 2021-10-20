package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import de.tierwohlteam.android.futterapp.models.Fridge
import de.tierwohlteam.android.futterapp.models.Pack
import de.tierwohlteam.android.futterapp.models.PacksInFridge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Dao
interface FridgeDao {

    @Transaction
    @Query("SELECT * from drawer where amount > 0")
    fun allDrawers(): Flow<List<Fridge.FoodInDrawer>>

    @Transaction
    @Query("SELECT * from drawer")
    fun allDrawersWithZero(): Flow<List<Fridge.FoodInDrawer>>

    @Query("SELECT * from drawer where foodID = :foodID and packSize = :packSize")
    suspend fun drawer(foodID: Uuid, packSize: Int): Fridge.Drawer?

    @Insert(onConflict = REPLACE)
    suspend fun insertDrawer(drawer: Fridge.Drawer)

    fun content(): Flow<List<PacksInFridge>> = allDrawers().map { list ->
            list.map { PacksInFridge(Pack(it.food, it.drawer.packSize), it.drawer.amount) }
    }

    fun contentWithEmpty(): Flow<List<PacksInFridge>> = allDrawersWithZero().map { list ->
        list.map { PacksInFridge(Pack(it.food, it.drawer.packSize), it.drawer.amount) }
    }
    suspend fun addPacks(pack: Pack, amount: Int = 1): PacksInFridge {
        val currentDrawer = drawer(pack.food.id, pack.size)
        val newAmount = (currentDrawer?.amount ?: 0) + amount
        insertDrawer(Fridge.Drawer(foodID = pack.food.id,
            packSize = pack.size,
            amount = newAmount,
            id = currentDrawer?.id ?: uuid4()
        ))
        return PacksInFridge(pack, newAmount)
    }

    suspend fun getPack(pack: Pack): PacksInFridge? {
        val currentDrawer = drawer(pack.food.id, pack.size)
        return if (currentDrawer == null || currentDrawer.amount == 0) {
            null
        } else {
            insertDrawer(Fridge.Drawer(foodID = pack.food.id,
                packSize = pack.size,
                amount = currentDrawer.amount - 1,
                id = currentDrawer.id
            ))
            PacksInFridge(pack, currentDrawer.amount - 1)
        }
    }
}
