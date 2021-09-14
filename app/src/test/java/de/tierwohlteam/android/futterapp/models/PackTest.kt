package de.tierwohlteam.android.futterapp.models

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class PackTest {
    @Test
    fun getType() {
        val type = "Rindermuskelfleisch Extra"
        val amount = 500
        val pack = Pack(type = type, amount = amount)
        assertThat(pack.type).isEqualTo(type)
    }

    @Test
    fun getAmount() {
        val type = "Rindermuskelfleisch Extra"
        val amount = 500
        val pack = Pack(type = type, amount = amount)
        assertThat(pack.amount).isEqualTo(amount)
    }
}