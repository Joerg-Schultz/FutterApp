package de.tierwohlteam.android.futterapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.*

@Entity(
    tableName = "rating"
)
/**
 * Rate the health status of your dog
 * @property[id] uuid for database
 * @property[timeStamp] LocalDateTime when the rating was performed
 * @property[value] Int from 1-5
 * @property[comment] String
 */
data class Rating(
    @PrimaryKey
    val id: Uuid = uuid4(),
    val timeStamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val value: Int,
    val comment: String) {
}