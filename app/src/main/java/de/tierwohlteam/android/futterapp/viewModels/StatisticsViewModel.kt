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
import kotlinx.coroutines.flow.*
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
    var allEntries: MutableStateFlow<Resource<List<CalendarEntry>>> = MutableStateFlow(value = Resource.loading(null))

    fun getEntries()  {
        val allCalendarEntries = mutableMapOf<LocalDate, CalendarEntry>()
        viewModelScope.launch {
            repository.allMeals.collect { allMeals ->
                if (allMeals.status == Status.SUCCESS) {
                    for (meal in allMeals.data!!) {
                        val mealDate = meal.feeding.time.date
                        val calendarEntry = allCalendarEntries.getOrPut(mealDate) { CalendarEntry(date = mealDate) }
                        calendarEntry.meals.add(meal)
                        allCalendarEntries[mealDate] = calendarEntry
                    }
                    allEntries.value = Resource.success(allCalendarEntries.values.toList())
                }
            }
        }
        viewModelScope.launch {
            repository.allRatings.collect { allRatings ->
                if (allRatings.status == Status.SUCCESS) {
                    for (rating in allRatings.data!!) {
                        val ratingDate = rating.timeStamp.date
                        val calendarEntry = allCalendarEntries.getOrPut(ratingDate) { CalendarEntry(date = ratingDate) }
                        calendarEntry.ratings.add(rating)
                        allCalendarEntries[ratingDate] = calendarEntry
                    }
                    allEntries.value = Resource.success(allCalendarEntries.values.toList())
                }
            }
        }
    }
}