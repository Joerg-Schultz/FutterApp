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

@Dao
interface FridgeDao {

    @Transaction
    @Query("SELECT * from drawer")
    suspend fun allDrawers(): List<Fridge.FoodInDrawer>

    @Query("SELECT * from drawer where foodID = :foodID and packSize = :packSize")
    suspend fun drawer(foodID: Uuid, packSize: Int): Fridge.Drawer?

    @Insert(onConflict = REPLACE)
    suspend fun insertDrawer(drawer: Fridge.Drawer)

    //TODO Return Flow here?
    suspend fun content(): List<PacksInFridge> =
        allDrawers().map {
            PacksInFridge(Pack(it.food, it.drawer.packSize), it.drawer.amount)
        }

    suspend fun addPack(pack: Pack): PacksInFridge {
        val currentDrawer = drawer(pack.food.id, pack.size)
        val newAmount = (currentDrawer?.amount ?: 0) + 1
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
