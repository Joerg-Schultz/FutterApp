package de.tierwohlteam.android.futterapp.models

import androidx.room.*
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import javax.inject.Inject


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
 * access via repository functions
 */
object Fridge {

    @Entity(
        tableName = "drawer",
        foreignKeys = [ForeignKey(
            entity = Food::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("foodID")
        )],
        indices = [Index(value = ["foodID", "packSize"], unique = true)]
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
}