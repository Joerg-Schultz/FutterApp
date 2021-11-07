package de.tierwohlteam.android.futterapp.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class FridgeViewModel @Inject constructor(
    private val repository: FutterAppRepository,
) : ViewModel() {

    private val _insertPacksFlow: MutableSharedFlow<Resource<PacksInFridge>> = MutableSharedFlow()
    val insertPacksFlow = _insertPacksFlow as SharedFlow<Resource<PacksInFridge>>

    private val _deletePackFlow: MutableSharedFlow<Resource<PacksInFridge>> = MutableSharedFlow()
    val deletePacksFlow = _deletePackFlow as SharedFlow<Resource<PacksInFridge>>

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
        _insertPacksFlow.emit(Resource.loading(null))
        var pack: Pack? = null
        val foodJob = viewModelScope.launch {
            val food = repository.getFoodByNameAndType(type = foodType, name = foodName)
            pack = Pack(food, gram)
        }
        foodJob.join()
        viewModelScope.launch {
            try {
                val packsInFridge = repository.addPacksToFridge(pack!!, amount)
                _insertPacksFlow.emit(Resource.success(packsInFridge))
            } catch (e: Throwable) {
                _insertPacksFlow.emit(Resource.error("Could not insert packs", null))
            }

        }
    }

    fun deleteOnePack(pos: Int) {
        val pack = content.value.data?.get(pos)?.pack
        if (pack != null) {
            viewModelScope.launch {
                try {
                    val packInFridge = repository.getPackFromFridge(pack)
                    _deletePackFlow.emit(Resource.success(packInFridge))
                } catch (e: Throwable) {
                    _insertPacksFlow.emit(Resource.error("Could not retrieve pack", null))
                }
            }
        }
    }

}