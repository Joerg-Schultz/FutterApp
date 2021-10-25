package de.tierwohlteam.android.futterapp.others

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.models.FoodType
import java.text.DecimalFormat
import java.time.DayOfWeek

fun FoodType.translate(context: Context): String =
    when (this) {
        FoodType.MEAT -> context.getString(R.string.meat)
        FoodType.CARBS -> context.getString(R.string.carbs)
        FoodType.VEGGIES_COOKED -> context.getString(R.string.veggiesCooked)
        FoodType.VEGGIES_RAW -> context.getString(R.string.veggiesRaw)
        FoodType.ADD_ONS -> context.getString(R.string.addons)
        FoodType.OTHERS -> context.getString((R.string.others))
    }

fun FoodType.icon(context: Context): Drawable? =
    when (this) {
        FoodType.MEAT -> context.getDrawable(R.mipmap.ic_meat_foreground)
        FoodType.CARBS -> context.getDrawable(R.mipmap.ic_bread_foreground)
        FoodType.VEGGIES_RAW -> context.getDrawable(R.mipmap.ic_fruit_foreground)
        FoodType.VEGGIES_COOKED -> context.getDrawable(R.mipmap.ic_carrot_foreground)
        FoodType.ADD_ONS -> context.getDrawable(R.mipmap.ic_addons_foreground)
        else -> null
    }

fun DayOfWeek.translate(context: Context, short: Boolean = false): String {
    val result = when (this) {
        DayOfWeek.MONDAY -> context.getString(R.string.monday)
        DayOfWeek.TUESDAY -> context.getString(R.string.tuesday)
        DayOfWeek.WEDNESDAY -> context.getString(R.string.wednesday)
        DayOfWeek.THURSDAY -> context.getString(R.string.thursday)
        DayOfWeek.FRIDAY -> context.getString(R.string.friday)
        DayOfWeek.SATURDAY -> context.getString(R.string.saturday)
        DayOfWeek.SUNDAY -> context.getString(R.string.sunday)
    }
    return if (short) result.substring(0..2) else result
}

fun Int.minute(): String = DecimalFormat("00").format(this)

// https://medium.com/@johanneslagos/dp-to-px-and-viceversa-for-kotlin-d797815d852b
val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()