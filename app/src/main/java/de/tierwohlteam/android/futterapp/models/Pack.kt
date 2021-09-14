package de.tierwohlteam.android.futterapp.models

/**
 * food packages
 * @property[food] Food
 * @property[amount] Int grams
 */
data class Pack(val food: Food, val amount:Int) {
    init {
      check(food.group == FoodType.MEAT)
    }
}