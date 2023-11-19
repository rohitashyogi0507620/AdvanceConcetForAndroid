package com.yogify.advanceconcetforandroid.api

import com.yogify.advanceconcetforandroid.models.BusStopResponse
import com.yogify.advanceconcetforandroid.utils.Constants
import com.yogify.advanceconcetforandroid.utils.Constants.BUS_STOP
import retrofit2.Response
import retrofit2.http.GET

interface ApiService{

    @GET(BUS_STOP)
    suspend fun getBusStopList(): Response<BusStopResponse>
}