package de.tierwohlteam.android.futterapp.others

import android.content.Context
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.models.FoodType

fun translateFoodTypeHelper(type: FoodType, context: Context): String =
    when (type) {
        FoodType.MEAT -> context.getString(R.string.meat)
        FoodType.CARBS -> context.getString(R.string.carbs)
        FoodType.VEGGIES_COOKED -> context.getString(R.string.veggiesCooked)
        FoodType.VEGGIES_RAW -> context.getString(R.string.veggiesRaw)
        FoodType.OTHERS -> context.getString((R.string.others))
    }