package de.tierwohlteam.android.futterapp.models

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Test

class MealTest {

    @Test
    fun getIngredients() {
        val meal = Meal()
        val meat = Food(FoodType.MEAT, "Rindermuskel")
        val meatGrams = 250
        val carbs = Food(FoodType.CARBS, "Buchweizen")
        val carbGrams = 50
        meal.addIngredient(meat, meatGrams)
        meal.addIngredient(carbs, carbGrams)
        val mealMeat = meal.ingredients.filter { it.food.group == FoodType.MEAT }
        assertThat(mealMeat.first().food).isEqualTo(meat)
    }

    @Test
    fun getFeedingTime() {
        val feedingTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val meal = Meal(feeding = Feeding(feedingTime))
        val meat = Food(FoodType.MEAT, "Rindermuskel")
        val meatGrams = 250
        val carbs = Food(FoodType.CARBS, "Buchweizen")
        val carbGrams = 50
        meal.addIngredient(meat, meatGrams)
        meal.addIngredient(carbs, carbGrams)
        assertThat(meal.feeding.time).isEqualTo(feedingTime)
    }
}