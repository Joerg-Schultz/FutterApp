package de.tierwohlteam.android.futterapp.viewModels

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.futterapp.models.Feeding
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.Meal
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.others.Resource
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayAt
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: FutterAppRepository,
) : ViewModel() {

    data class CalendarEntry(
        val date: LocalDate,
        val ratings: MutableList<Rating> = mutableListOf(),
        val meals: MutableList<Meal> = mutableListOf()
    )

    var allFoods: MutableStateFlow<Resource<List<Food>>> = MutableStateFlow(Resource.loading(emptyList()))

    var allEntries: StateFlow<Resource<List<CalendarEntry>>> =
        combine(repository.allRatings, repository.allMeals, repository.allFoods) {
                ratingResource, mealResource, foodResource ->
            setFoodTest(foodResource)
            val listTest: List<CalendarEntry> = buildCalendarEntries(mealResource, ratingResource)
            Resource.success(listTest)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(emptyList())
        )

    private fun setFoodTest(foodResource: Resource<List<Food>>) {
        if (foodResource.status == Status.SUCCESS) {
            allFoods.value = foodResource
        }
    }

    private fun buildCalendarEntries(mealResource: Resource<List<Meal>>, ratingResource: Resource<List<Rating>>) : List<CalendarEntry> {
        val calendarEntries =  mutableMapOf<LocalDate, CalendarEntry>()
        if (mealResource.status == Status.SUCCESS && ratingResource.status == Status.SUCCESS) {
            mealResource.data!!.forEach { meal ->
                val mealDate = meal.feeding.time.date
                val calendarEntry = calendarEntries.getOrPut(mealDate) { CalendarEntry(date = mealDate) }
                calendarEntry.meals.add(meal)
                calendarEntries[mealDate] = calendarEntry

            }
            ratingResource.data!!.forEach { rating ->
                val ratingDate = rating.timeStamp.date
                val calendarEntry =
                    calendarEntries.getOrPut(ratingDate) { CalendarEntry(date = ratingDate) }
                calendarEntry.ratings.add(rating)
                calendarEntries[ratingDate] = calendarEntry

            }
        }
        return calendarEntries.values.toList()
    }
}