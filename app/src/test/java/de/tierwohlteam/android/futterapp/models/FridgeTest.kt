package de.tierwohlteam.android.futterapp.models

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class FridgeTest {

    @Test
    fun getContent() {
        val fridge = Fridge
        assertThat(fridge.content()).isEmpty()
    }

    @Test
    fun addPack() {
        val fridge = Fridge
        val meatType = "RinderMuskelfleisch Extra"
        val grams = 500
        val pack = Pack(type = meatType, size = grams)
        fridge.addPack(pack = pack, amount = 2)
        fridge.addPack(pack = pack, amount = 2)
        val fridgeContent = fridge.content()
        assertThat(fridgeContent.size).isEqualTo(1)
        assertThat(fridgeContent.first().second).isEqualTo(4)
        val gotPack = fridgeContent.first().first
        assertThat(gotPack.food.type).isEqualTo(meatType)
        assertThat(gotPack.size).isEqualTo(grams)
    }

    @Test
    fun retrievePack(){
        val fridge = Fridge
        val meatType = "RinderMuskelfleisch Extra"
        val grams = 500
        val pack = Pack(type = meatType, size = grams)
        fridge.addPack(pack = pack, amount = 4)
        fridge.retrievePack(pack = pack)
        val fridgeContent = fridge.content()
        assertThat(fridgeContent.size).isEqualTo(1)
        val (_, fridgePackAmount) = fridgeContent.first()
        assertThat(fridgePackAmount).isEqualTo(3)
    }
}