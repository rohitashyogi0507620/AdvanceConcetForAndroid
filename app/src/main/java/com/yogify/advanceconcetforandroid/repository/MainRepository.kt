package com.yogify.advanceconcetforandroid.repository

import com.yogify.advanceconcetforandroid.api.ApiHelper
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiHelper: ApiHelper
){
    suspend fun getBusStopList() = apiHelper.getBusStopList()
}