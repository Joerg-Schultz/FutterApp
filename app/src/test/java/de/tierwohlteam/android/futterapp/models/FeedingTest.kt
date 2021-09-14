package de.tierwohlteam.android.futterapp.models

import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class FeedingTest {

    @Test
    fun getIngredients() {
        val meat = Food(FoodType.MEAT, "Rindermuskel")
        val meatGrams = 250
        val carbs = Food(FoodType.CARBS, "Buchweizen")
        val carbGrams = 50
        val feed = Feeding(listOf(Pair(meat, meatGrams), Pair(carbs, carbGrams)))
        val fedCarbs = feed.ingredients.first { it.first.group == FoodType.CARBS }
        assertThat(fedCarbs.first.type).isEqualTo("Buchweizen")
    }

    @Test
    fun getTimeStamp() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val meat = Food(FoodType.MEAT, "Rindermuskel")
        val meatGrams = 250
        val carbs = Food(FoodType.CARBS, "Buchweizen")
        val carbGrams = 50
        val feed = Feeding(listOf(Pair(meat, meatGrams), Pair(carbs, carbGrams)), now)
        assertThat(feed.timeStamp).isEqualTo(now)
    }
}