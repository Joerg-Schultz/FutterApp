package de.tierwohlteam.android.futterapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.futterapp.models.*
import de.tierwohlteam.android.futterapp.others.Event
import de.tierwohlteam.android.futterapp.others.Resource
import de.tierwohlteam.android.futterapp.others.Status
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

    init {
        val latestMeal: StateFlow<Resource<Meal?>> = repository.latestMeal.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )

        viewModelScope.launch {
            latestMeal.collect{ result ->
                if (result.status == Status.SUCCESS) {
                    emptyIngredientList()
                    val ingredients = result.data?.ingredients ?: emptyList()
                    for (ingredient in ingredients) {  //data CAN bes null here
                        val food = allFoods.value.data?.firstOrNull() {it.id == ingredient.foodID}
                        if (food != null) addIngredient(food.group, food.name, ingredient.gram)
                    }
                }

            }
        }
    }
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

    var allMeals: StateFlow<Resource<List<Meal>>> = repository.allMeals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(emptyList())
    )

    var allFoods: StateFlow<Resource<List<Food>>> = repository.allFoods.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(emptyList())
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

    fun deleteIngredient(pos: Int) {
        val newList = _ingredientList.value.toMutableList()
        newList.removeAt(pos)
        _ingredientList.value = newList
    }

    fun emptyIngredientList() {
        _ingredientList.value = emptyList()
    }
}