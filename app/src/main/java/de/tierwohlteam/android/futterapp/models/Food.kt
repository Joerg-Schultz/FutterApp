package de.tierwohlteam.android.futterapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The food fed to the dog
 * @property[group] FoodType classification
 * @property[name] the actual food, e.g. carrot
 */
@Entity(
    tableName = "food"
)
data class Food(
    val group: FoodType,
    @PrimaryKey
    val name: String) {
}

/**
 * Food types
 * Meat
 * carbs
 * cooked veggies
 * raw veggies
 */
enum class FoodType {
    MEAT,
    CARBS,
    VEGGIES_COOKED,
    VEGGIES_RAW
}