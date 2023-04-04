package com.idplus.flyco2tracker.ui.simulation

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idplus.flyco2tracker.ui.adapter.Airport
import com.idplus.flyco2tracker.data.remote.repository.SimulationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import kotlin.math.roundToInt



@HiltViewModel
class SimulationViewModel @Inject constructor(
    private val repository: SimulationRepository
)
    : ViewModel() {

    companion object {
        const val TAG = "SimulationViewModel"
    }

    private var latitudeFrom: Double = 0.0
    private var longitudeFrom: Double = 0.0
    private var latitudeTo: Double = 0.0
    private var longitudeTo: Double = 0.0

    var isReturnTrip: Boolean = false
    var comfortValue: String = ""
    var airportDataLoaded: Boolean = false
    val airportList: ArrayList<Airport> = mutableListOf<Airport>() as ArrayList<Airport>

    // initialize distance value live data to display in the layout
    private val _distanceValueKm = MutableLiveData(0)
    val distanceValueKm: LiveData<Int> get() = _distanceValueKm

    private var _pictureCityUrl = MutableLiveData("")
    val pictureCityUrl: LiveData<String> get() = _pictureCityUrl

    /**
     *
     */
    fun readAirportDataFile(input: InputStream) {
        var string: String? = ""
        val reader = BufferedReader(InputStreamReader(input))

        if(airportDataLoaded)
            return

        Log.d(TAG, "uploading information from airport data local file")
        while (true) {
            try {
                if (reader.readLine().also { string = it } == null)
                    break
            } catch (e: IOException) {
                e.printStackTrace()
            }
            // if the string is not null
            string?.let {
                // then we can split the line to get the name, country & city of the airport
                val airportInfo: List<String> = it.replace("\"", "").split(";")
                val airport = Airport()
                airport.name = airportInfo[Airport.ParsingKeys.AIRPORT_NAME_ID]
                airport.city = airportInfo[Airport.ParsingKeys.CITY_NAME_ID]
                airport.country = airportInfo[Airport.ParsingKeys.COUNTRY_NAME_ID]
                airport.iata = airportInfo[Airport.ParsingKeys.COUNTRY_IATA_ID]
                airport.latitude = airportInfo[Airport.ParsingKeys.LATITUDE_ID].toDoubleOrNull()!!
                airport.longitude = airportInfo[Airport.ParsingKeys.LONGITUDE_ID].toDoubleOrNull()!!
                airportList.add(airport)
            }
        }
        input.close()

        airportDataLoaded = true
    }

    fun setAirportDeparture(airport: Airport) {
        latitudeFrom = airport.latitude
        longitudeFrom = airport.longitude
        Log.d(TAG, "setting departure airport latitude ($latitudeFrom) & longitude ($longitudeFrom)")
        if(latitudeTo != 0.0 && longitudeTo != 0.0)
            calculateDistanceBetweenAirports()
    }

    fun setAirportArrival(airport: Airport) {
        latitudeTo = airport.latitude
        longitudeTo = airport.longitude
        Log.d(TAG, "setting departure airport latitude ($latitudeTo) & longitude ($longitudeTo)")
        if(latitudeFrom != 0.0 && longitudeFrom != 0.0)
            calculateDistanceBetweenAirports()

        // fetching the URL of the airport city's picture from internet
        viewModelScope.launch {
            _pictureCityUrl.value = repository.getPictureUrlOfAirportCity(airport.city)
        }
    }

    /**
     *
     */
    private fun calculateDistanceBetweenAirports() {
        val results = FloatArray(1)
        Location.distanceBetween(latitudeFrom, longitudeFrom, latitudeTo, longitudeTo, results)
        _distanceValueKm.value = (results[0] / 1000).roundToInt()
        Log.d(TAG, "distance between the 2 airports : ${_distanceValueKm.value} kilometers")
    }

    fun clearTotalDistance() {
        Log.d(TAG, "clearing total distance in kms")
        _distanceValueKm.value = 0
    }

    override fun onCleared() {
        Log.i(TAG, "view model cleared")
    }

    fun setFlightPreferences(isReturn: Boolean, comfortClass: String) {
        isReturnTrip = isReturn
        comfortValue = comfortClass
    }
}