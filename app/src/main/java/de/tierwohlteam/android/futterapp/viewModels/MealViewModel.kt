package de.tierwohlteam.android.futterapp.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.futterapp.models.*
import de.tierwohlteam.android.futterapp.others.Event
import de.tierwohlteam.android.futterapp.others.Resource
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MealViewModel @Inject constructor(
    private val repository: FutterAppRepository,
) : ViewModel() {

    // Combines Ingredients with name + group of food
    inner class MealComponent(
        val foodGroup: FoodType,
        val foodName: String,
        val foodID: Uuid?,
        val gram: Int
    ) { }
    private val _ingredientList: MutableStateFlow<List<MealComponent>> = MutableStateFlow(emptyList())
    val ingredientList: StateFlow<List<MealComponent>> = _ingredientList

    private val _insertMealFlow: MutableStateFlow<Event<Resource<Meal>>> = MutableStateFlow(Event(Resource.empty()))
    val insertMealFlow = _insertMealFlow as StateFlow<Event<Resource<Meal>>>
    var allMeals: StateFlow<Resource<List<Meal>>> = MutableStateFlow(Resource.empty())
    var allFoods: StateFlow<List<Food>> =
            repository.allFoods().stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )



    suspend fun saveMeal() {
        _insertMealFlow.value = Event(Resource.loading(null))
        val meal = Meal()
        val ingredientJob = viewModelScope.launch {
            for (component in _ingredientList.value) {
                val food = repository.getFoodByNameAndType(type = component.foodGroup, name = component.foodName)
                meal.addIngredient(food, component.gram)
            }
        }
        ingredientJob.join() //otherwise there might be foreign key problems
        viewModelScope.launch {
            try {
                repository.insertMeal(meal)
                _insertMealFlow.value = Event(Resource.success(meal))
            } catch (e: Throwable) {
                _insertMealFlow.value = Event(Resource.error("Could no insert meal", meal))
            }

        }
    }
    fun addIngredient(currentFoodType: FoodType, currentFoodName: String, currentGram: Int) {
        _ingredientList.value = _ingredientList.value +
                MealComponent(currentFoodType, currentFoodName, null, currentGram)
    }

    fun getAllMeals() {
        viewModelScope.launch {
            allMeals = repository.allMeals.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.loading(emptyList()))
        }
    }

    fun ingredientToComponent(ingredient: Ingredient): MealComponent? {
        Log.d("FOOD", "translating ${ingredient.foodID}")
        allFoods.value.forEach { Log.d("FOOD", " ${it.name}") }
        val food = allFoods.value.firstOrNull { it.id == ingredient.foodID }
        return if ( food == null ) null else MealComponent(food.group, food.name, food.id, ingredient.gram)
    }
}