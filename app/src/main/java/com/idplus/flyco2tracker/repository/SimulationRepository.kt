package com.idplus.flyco2tracker.repository

import android.util.Log
import com.idplus.flyco2tracker.api.AirportApi
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val TAG: String = "SimulationRepository"

class SimulationRepository {

    suspend fun getPictureUrlOfAirportCity(cityName: String): String? {
        var cityPictureUrl: String? = null
        withContext(Dispatchers.IO) {
            try {
                val responseCitySearch = AirportApi.service.getPictureCityFromWikipedia(cityName).await()
                if (responseCitySearch.isSuccessful) {
                    cityPictureUrl = responseCitySearch.body()!!.pages[0].thumbnail.url
                    Log.d(TAG, "picture url found from wikipedia : $cityPictureUrl")
                }
                else
                    Log.d(TAG, "response from wikipedia api was unsuccessful")
            }
            catch(e: JsonDataException) {
                Log.d(TAG, "there was an error while parsing the JSON response")
            }
        }
        return cityPictureUrl
    }
}