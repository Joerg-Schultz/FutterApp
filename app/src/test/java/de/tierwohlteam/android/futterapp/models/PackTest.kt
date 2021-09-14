package de.tierwohlteam.android.futterapp.models

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class PackTest {
    @Test
    fun getType() {
        val meatType = "Rindermuskelfleisch Extra"
        val amount = 500
        val pack = Pack(type = meatType, size = amount)
        assertThat(pack.food.group).isEqualTo(FoodType.MEAT)
        assertThat(pack.food.type).isEqualTo(meatType)
    }

    @Test
    fun getAmount() {
        val meatType = "Rindermuskelfleisch Extra"
        val amount = 500
        val pack = Pack(type = meatType, size = amount)
        assertThat(pack.size).isEqualTo(amount)
    }
}