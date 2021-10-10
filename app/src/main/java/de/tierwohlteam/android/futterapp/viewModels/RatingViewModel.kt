package de.tierwohlteam.android.futterapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.others.Event
import de.tierwohlteam.android.futterapp.others.Resource
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class RatingViewModel @Inject constructor(
    private val repository: FutterAppRepository,
) : ViewModel() {

    var allRatings: StateFlow<Resource<List<Rating>>> = MutableStateFlow(Resource.empty())
    private val _insertRatingFlow: MutableStateFlow<Event<Resource<Rating>>> = MutableStateFlow(Event(Resource.empty()))
    val insertRatingFlow = _insertRatingFlow as StateFlow<Event<Resource<Rating>>>

    fun getAllRatings() {
        viewModelScope.launch {
            allRatings = repository.allRatings.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.loading(emptyList()))
        }
    }

    suspend fun insertRating(value: Float, comment: String) {
        if(value < 0) {
            _insertRatingFlow.value = Event(Resource.error("Rating has to be positive or null", null))
            return
        }
        val rating = Rating(value = value, comment =  comment)
        viewModelScope.launch {
            repository.insertRating(rating)
            _insertRatingFlow.value = Event(Resource.success(rating))
        }
    }
}
