package de.tierwohlteam.android.futterapp.models

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Test
import com.google.common.truth.Truth.assertThat


internal class RatingTest {

    @Test
    fun getTimeStamp() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val rating = Rating(timeStamp = now, value = 3, comment = "good")
        assertThat(rating.timeStamp).isEqualTo(now)
    }

    @Test
    fun getValue() {
        val value = 3
        val rating = Rating(value = value, comment = "good")
        assertThat(rating.value).isEqualTo(value)
    }

    @Test
    fun getComment() {
        val comment = "good"
        val rating = Rating(value = 3, comment = comment)
        assertThat(rating.comment).isEqualTo(comment)
    }
}