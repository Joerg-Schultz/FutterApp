package de.tierwohlteam.android.futterapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.futterapp.fragments.AddMealFragment
import de.tierwohlteam.android.futterapp.models.Feeding
import de.tierwohlteam.android.futterapp.models.Ingredient
import de.tierwohlteam.android.futterapp.models.Meal
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.others.Event
import de.tierwohlteam.android.futterapp.others.Resource
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MealViewModel @Inject constructor(
    private val repository: FutterAppRepository,
) : ViewModel() {

    private val _insertMealFlow: MutableStateFlow<Event<Resource<Meal>>> = MutableStateFlow(Event(Resource.empty()))
    val insertMealFlow = _insertMealFlow as StateFlow<Event<Resource<Meal>>>

    suspend fun saveMeal(componentList: List<AddMealFragment.MealComponent>) {
        _insertMealFlow.value = Event(Resource.loading(null))
        val meal = Meal()
        val ingredientJob = viewModelScope.launch {
            for (component in componentList) {
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
}