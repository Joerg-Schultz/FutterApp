package de.tierwohlteam.android.futterapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FutterAppRepository,
) : ViewModel() {
    fun emptyDatabase() {
        viewModelScope.launch {
            repository.emptyDatabase()
        }
    }
}