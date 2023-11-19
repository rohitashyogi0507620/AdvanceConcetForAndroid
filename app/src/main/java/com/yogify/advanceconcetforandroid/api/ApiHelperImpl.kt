package com.yogify.advanceconcetforandroid.api

import com.yogify.advanceconcetforandroid.models.BusStopResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {
    override suspend fun getBusStopList(): Response<BusStopResponse> = apiService.getBusStopList()
}