package com.idplus.flyco2tracker.data.remote.api

import com.idplus.flyco2tracker.data.remote.model.ResponseCityResult
import com.idplus.flyco2tracker.utils.Constants.Companion.URL_BASE
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AirportApiService {

    @GET("core/v1/wikipedia/en/search/title")
    fun getPictureCityFromWikipedia(
        @Query("q") query: String,
        @Query("limit") limit: Int = 1
    )
        : Deferred<Response<ResponseCityResult>>
}

object AirportApi {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val service: AirportApiService = retrofit.create(AirportApiService::class.java)
}