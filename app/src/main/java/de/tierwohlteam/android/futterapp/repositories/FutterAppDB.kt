package de.tierwohlteam.android.futterapp.repositories

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import de.tierwohlteam.android.futterapp.models.*
import de.tierwohlteam.android.futterapp.repositories.daos.FoodDao
import de.tierwohlteam.android.futterapp.repositories.daos.FridgeDao
import de.tierwohlteam.android.futterapp.repositories.daos.MealDao
import de.tierwohlteam.android.futterapp.repositories.daos.RatingDao
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

/**
 * Build the Room database for FutterApp
 * Actual start of the db in AppModule ->Hilt
 */
@Database(
    entities = [
        Rating::class,
        Food::class,
        Feeding::class,
        Ingredient::class,
        Fridge.Drawer::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FutterAppDB : RoomDatabase() {
    abstract fun ratingDao(): RatingDao
    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
    abstract fun fridgeDao():FridgeDao
}
class Converters {
    @TypeConverter
    fun toUUID(uuidString: String?): Uuid? {
        return if(uuidString != null) uuidFrom(uuidString) else null
    }

    @TypeConverter
    fun fromUUID(uuid: Uuid?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toString(localDateTime: LocalDateTime): String {
        return localDateTime.toString()
    }

    @TypeConverter
    fun fromString(localDateTimeString: String): LocalDateTime {
        return localDateTimeString.toLocalDateTime()
    }
}