package de.tierwohlteam.android.futterapp.models

import kotlinx.datetime.*

/**
 * Rate the health status of your dog
 * @property[timeStamp] LocalDateTime when the rating was performed
 * @property[value] Int from 1-5
 * @property[comment] String
 */
data class Rating(
    val timeStamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val value: Int,
    val comment: String) {
}