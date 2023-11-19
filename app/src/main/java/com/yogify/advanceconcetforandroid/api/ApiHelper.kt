package com.yogify.advanceconcetforandroid.api

import com.yogify.advanceconcetforandroid.models.BusStopResponse
import retrofit2.Response

interface ApiHelper {
    suspend fun getBusStopList(): Response<BusStopResponse>
}