package de.tierwohlteam.android.futterapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.futterapp.models.*
import de.tierwohlteam.android.futterapp.others.Event
import de.tierwohlteam.android.futterapp.others.Resource
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class FridgeViewModel @Inject constructor(
    private val repository: FutterAppRepository,
) : ViewModel() {

    private val _insertPacksFlow: MutableStateFlow<Event<Resource<PacksInFridge>>> = MutableStateFlow(Event(Resource.empty()))
    val insertPacksFlow = _insertPacksFlow as StateFlow<Event<Resource<PacksInFridge>>>

    private val _deletePackFlow: MutableStateFlow<Event<Resource<PacksInFridge>>> = MutableStateFlow(Event(Resource.empty()))
    val deletePacksFlow = _deletePackFlow as StateFlow<Event<Resource<PacksInFridge>>>

    val content: StateFlow<Resource<List<PacksInFridge>>> = repository.fridgeContent.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(emptyList())
    )
    val contentWithEmpty: StateFlow<Resource<List<PacksInFridge>>> = repository.fridgeContentWithEmpty.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(emptyList())
    )
    suspend fun addToFridge(foodType: FoodType, foodName: String, gram: Int, amount: Int) {
        _insertPacksFlow.value = Event(Resource.loading(null))
        var pack: Pack? = null
        val foodJob = viewModelScope.launch {
            val food = repository.getFoodByNameAndType(type = foodType, name = foodName)
            pack = Pack(food, gram)
        }
        foodJob.join()
        viewModelScope.launch {
            try {
                val packsInFridge = repository.addPacksToFridge(pack!!, amount)
                _insertPacksFlow.value = Event(Resource.success(packsInFridge))
            } catch (e: Throwable) {
                _insertPacksFlow.value = Event(Resource.error("Could not insert packs", null))
            }

        }
    }

    fun deleteOnePack(pos: Int) {
        val pack = content.value.data?.get(pos)?.pack
        if (pack != null) {
            viewModelScope.launch {
                try {
                    val packInFridge = repository.getPackFromFridge(pack)
                    _deletePackFlow.value = Event(Resource.success(packInFridge))
                } catch (e: Throwable) {
                    _insertPacksFlow.value = Event(Resource.error("Could not retrieve pack", null))
                }
            }
        }
    }

}