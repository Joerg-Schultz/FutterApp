package de.tierwohlteam.android.futterapp.others

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.models.FoodType

fun translateFoodTypeHelper(type: FoodType, context: Context): String =
    when (type) {
        FoodType.MEAT -> context.getString(R.string.meat)
        FoodType.CARBS -> context.getString(R.string.carbs)
        FoodType.VEGGIES_COOKED -> context.getString(R.string.veggiesCooked)
        FoodType.VEGGIES_RAW -> context.getString(R.string.veggiesRaw)
        FoodType.ADD_ONS -> context.getString(R.string.addons)
        FoodType.OTHERS -> context.getString((R.string.others))
    }

// TODO add function for icons here
fun iconFoodTypeHelper(type: FoodType, context: Context): Drawable? =
    when (type) {
        FoodType.MEAT -> context.getDrawable(R.mipmap.ic_meat_foreground)
        FoodType.CARBS -> context.getDrawable(R.mipmap.ic_bread_foreground)
        FoodType.VEGGIES_RAW -> context.getDrawable(R.mipmap.ic_fruit_foreground)
        FoodType.VEGGIES_COOKED -> context.getDrawable(R.mipmap.ic_carrot_foreground)
        FoodType.ADD_ONS -> context.getDrawable(R.mipmap.ic_addons_foreground)
        else -> null
    }

// https://medium.com/@johanneslagos/dp-to-px-and-viceversa-for-kotlin-d797815d852b
val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()