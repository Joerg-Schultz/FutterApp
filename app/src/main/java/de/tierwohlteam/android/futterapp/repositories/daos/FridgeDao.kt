package de.tierwohlteam.android.futterapp.repositories.daos

import androidx.room.Dao
import androidx.room.Query
import de.tierwohlteam.android.futterapp.models.Fridge
import de.tierwohlteam.android.futterapp.models.Pack
import de.tierwohlteam.android.futterapp.models.PacksInFridge

@Dao
interface FridgeDao {

    @Query("SELECT * from drawer")
    suspend fun drawers(): List<Fridge.FoodInDrawer>

    //TODO Return Flow here?
    suspend fun content(): List<PacksInFridge> =
        drawers().map {
            PacksInFridge(Pack(it.food, it.drawer.packSize), it.drawer.amount)
        }
}
