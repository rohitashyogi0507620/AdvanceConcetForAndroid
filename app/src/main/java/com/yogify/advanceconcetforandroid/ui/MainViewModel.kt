package com.yogify.advanceconcetforandroid.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogify.advanceconcetforandroid.models.BusStopResponse
import com.yogify.advanceconcetforandroid.repository.MainRepository
import com.yogify.advanceconcetforandroid.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _res = MutableLiveData<Resource<BusStopResponse>>()
    val res: LiveData<Resource<BusStopResponse>> get() = _res

    init {
        getBusStopList()
    }

   private fun getBusStopList() = viewModelScope.launch {
        _res.postValue(Resource.loading(null))
        mainRepository.getBusStopList().let {
            if (it.isSuccessful) {
                _res.postValue(Resource.success(it.body()))
            } else {
                _res.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

}
