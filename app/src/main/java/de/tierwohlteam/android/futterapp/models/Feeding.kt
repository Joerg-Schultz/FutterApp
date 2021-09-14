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
    val ingredients: List<Pair<Food,Int>>,
    val timeStamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
) {
}