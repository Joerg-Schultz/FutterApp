package de.tierwohlteam.android.futterapp.models

import com.benasher44.uuid.uuid4
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class PackTest {

    @Test
    fun getFood() {
        val food = Food(FoodType.MEAT, "Pute")
        val pack = Pack(food, 500)
        assertThat(pack.food).isEqualTo(food)
    }

    @Test
    fun getSize() {
        val size = 500
        val food = Food(FoodType.MEAT, "Pute")
        val pack = Pack(food, size)
        assertThat(pack.size).isEqualTo(size)
    }
}

class PacksInFridgeTest {

    @Test
    fun getPack() {
        val amount = 4
        val pack = Pack(Food(FoodType.MEAT, "Pute"), 500)
        val packInFridge = PacksInFridge(pack, amount)
        assertThat(packInFridge.pack).isEqualTo(pack)
    }

    @Test
    fun getAmount() {
        val amount = 4
        val pack = Pack(Food(FoodType.MEAT, "Pute"), 500)
        val packInFridge = PacksInFridge(pack, amount)
        assertThat(packInFridge.amount).isEqualTo(amount)
    }
}

class FridgeTest {

    @Test
    fun getDrawerFoodID() {
        val food = Food(FoodType.MEAT, "Pute")
        val packSize = 500
        val amount = 4
        val drawerID = uuid4()
        val drawer = Fridge.Drawer(food.id, packSize, amount, drawerID)
        assertThat(drawer.foodID).isEqualTo(food.id)
    }

    @Test
    fun getDrawerPackSize() {
        val food = Food(FoodType.MEAT, "Pute")
        val packSize = 500
        val amount = 4
        val drawerID = uuid4()
        val drawer = Fridge.Drawer(food.id, packSize, amount, drawerID)
        assertThat(drawer.packSize).isEqualTo(packSize)
    }

    @Test
    fun getDrawerAmount() {
        val food = Food(FoodType.MEAT, "Pute")
        val packSize = 500
        val amount = 4
        val drawerID = uuid4()
        val drawer = Fridge.Drawer(food.id, packSize, amount, drawerID)
        assertThat(drawer.amount).isEqualTo(amount)
    }

    @Test
    fun getDrawerID() {
        val food = Food(FoodType.MEAT, "Pute")
        val packSize = 500
        val amount = 4
        val drawerID = uuid4()
        val drawer = Fridge.Drawer(food.id, packSize, amount, drawerID)
        assertThat(drawer.id).isEqualTo(drawerID)
    }

    @Test
    fun getFoodInDrawerDrawer() {
        val food = Food(FoodType.MEAT, "Pute")
        val packSize = 500
        val amount = 4
        val drawerID = uuid4()
        val drawer = Fridge.Drawer(food.id, packSize, amount, drawerID)
        val foodInDrawer = Fridge.FoodInDrawer(drawer,food)
        assertThat(foodInDrawer.drawer).isEqualTo(drawer)
    }

    @Test
    fun getFoodInDrawerFood() {
        val food = Food(FoodType.MEAT, "Pute")
        val packSize = 500
        val amount = 4
        val drawerID = uuid4()
        val drawer = Fridge.Drawer(food.id, packSize, amount, drawerID)
        val foodInDrawer = Fridge.FoodInDrawer(drawer,food)
        assertThat(foodInDrawer.food).isEqualTo(food)
    }

}
