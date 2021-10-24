package de.tierwohlteam.android.futterapp.models

import android.content.res.Resources
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import de.tierwohlteam.android.futterapp.R

/**
 * The food fed to the dog
 * @property[group] FoodType classification
 * @property[name] the actual food, e.g. carrot
 */
@Entity(
    tableName = "food",
    indices = [Index(value = ["group", "name"], unique = true)] // uniq
)
data class Food(
    val group: FoodType,
    val name: String,
    @PrimaryKey
    val id: Uuid = uuid4()
) {
}

/**
 * Food types
 * Meat
 * carbs
 * cooked veggies
 * raw veggies
 * other stuff
 */
enum class FoodType {
    MEAT,
    CARBS,
    VEGGIES_COOKED,
    VEGGIES_RAW,
    OTHERS
}