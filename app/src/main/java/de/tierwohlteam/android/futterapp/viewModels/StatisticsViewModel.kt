package de.tierwohlteam.android.futterapp.viewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.futterapp.models.Feeding
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        val ratings: List<Rating> = emptyList(),
        val meals: List<Feeding> = emptyList()
    )
    val testList: List<CalendarEntry> = listOf(
        CalendarEntry(Clock.System.todayAt(TimeZone.currentSystemDefault())),
        CalendarEntry(Clock.System.todayAt(TimeZone.currentSystemDefault())),
        CalendarEntry(Clock.System.todayAt(TimeZone.currentSystemDefault())),
    )
}