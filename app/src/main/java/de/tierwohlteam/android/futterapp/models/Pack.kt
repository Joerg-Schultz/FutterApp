package de.tierwohlteam.android.futterapp.models

/**
 * food packages
 * @property[type] e.g. "Rindermuskelfleisch Extra"
 * @property[amount] Int grams
 */
data class Pack(val type: String, val amount:Int) {
}