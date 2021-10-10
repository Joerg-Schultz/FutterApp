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

    private val _insertRatingStatus = MutableLiveData<Event<Resource<Rating>>>(Event(Resource.empty()))
    val insertRatingStatus: LiveData<Event<Resource<Rating>>> = _insertRatingStatus

    private var _allRatings: MutableStateFlow<List<Rating>> =
        MutableStateFlow(value = emptyList())
    var allRatings: StateFlow<List<Rating>> = _allRatings

    fun getAllRatings() {
        viewModelScope.launch {
            Log.d("RATINGS", "start collecting")
            allRatings = repository.allRatings.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList())
        }
    }

    fun insertRating(value: Int, comment: String) {
        if(value < 0) {
            _insertRatingStatus.postValue(Event(Resource.error("Rating has to be positive or null", null)))
            return
        }
        val rating = Rating(value = value, comment =  comment)
        viewModelScope.launch {
            repository.insertRating(rating)
            Log.d("RATINGS", "insert Rating $value")
            _insertRatingStatus.postValue(Event(Resource.success(rating)))
            Log.d("RATINGS", "inserted Rating $value")
        }
    }
}
