package de.tierwohlteam.android.futterapp.models

/**
 * The food fed to the dog
 * @property[group] FoodType classification
 * @property[type] the actual food, e.g. carrot
 */
data class Food(val group: FoodType, val type: String) {
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