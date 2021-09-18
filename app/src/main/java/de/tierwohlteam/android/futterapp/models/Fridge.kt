package de.tierwohlteam.android.futterapp.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4


/**
 * food packages
 * @property[food] Food
 * @property[size] Int grams
 */
class Pack(val food: Food, val size:Int) {
}

/**
 * how many of a pack is there in the fridge
 * helper class to give names to the properties
 * @property[pack]
 * @property[amount]
 */
data class PacksInFridge(
    val pack: Pack,
    val amount: Int
)

/**
 * The fridge containing packs of Food
 * implemented as Object, no params
 * There is also no Fridge unit test, as the functions depend on the database
 */
object Fridge {

    @Entity(
        tableName = "drawer"
    )
    data class Drawer(
        val foodID: Uuid,
        val packSize: Int,
        val amount: Int,
        @PrimaryKey
        val id: Uuid = uuid4()
    )

    data class FoodInDrawer(
        @Embedded
        val drawer: Drawer,
        @Relation(
            parentColumn = "foodID",
            entityColumn = "id"
        )
        val food: Food
    )
/*
    fun content(): List<PacksInFridge> = repository.fridgeContent()
    fun addPack(pack: Pack) = repository.addPackToFridge(pack)
    fun getPack(pack: Pack): Boolean = repository.getPackFromFridge(pack)
*/
}