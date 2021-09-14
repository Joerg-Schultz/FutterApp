package de.tierwohlteam.android.futterapp.models

/**
 * food packages
 * @property[food] Food
 * @property[size] Int grams
 */
class Pack(type: String, val size:Int) {
    val food = Food(group = FoodType.MEAT, type)
}