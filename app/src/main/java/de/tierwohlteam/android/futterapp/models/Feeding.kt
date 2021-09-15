package de.tierwohlteam.android.futterapp.models

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * What did you feed yor dog?
 * @property[ingredients] List of Pair Food and gram
 * @property[timeStamp] when? default now
 */
data class Feeding(
    val ingredients: List<Ingredient>,
    val timeStamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
) {
}

/**
 * Ingredient of food
 * @property[food] Food
 * @property[gram] Int
 */
data class Ingredient(val food: Food, val gram: Int)