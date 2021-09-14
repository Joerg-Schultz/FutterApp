package de.tierwohlteam.android.futterapp.models

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class FoodTest {

    @Test
    fun getGroup() {
        val foodType = FoodType.MEAT
        val meat = "Rindermuskelfleisch Extra"
        val testFood = Food(foodType, meat)
        assertThat(testFood.group).isEqualTo(foodType)
    }

    @Test
    fun getType() {
        val foodType = FoodType.MEAT
        val meat = "Rindermuskelfleisch Extra"
        val testFood = Food(foodType, meat)
        assertThat(testFood.type).isEqualTo(meat)
    }
}