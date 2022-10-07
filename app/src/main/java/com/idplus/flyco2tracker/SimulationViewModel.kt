package com.idplus.flyco2tracker

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.roundToInt

class SimulationViewModel : ViewModel() {

    val TAG = SimulationViewModel::class.java.toString()

    private var latitudeFrom: Double = 0.0
    private var longitudeFrom: Double = 0.0
    private var latitudeTo: Double = 0.0
    private var longitudeTo: Double = 0.0

    var isReturnTrip: Boolean = false
    var comfortValue: String = ""

    // initialise distance value live data to display in the layout
    private val _distanceValueKm = MutableLiveData(0)
    val distanceValueKm: LiveData<Int> get() = _distanceValueKm


    init {
    }

    /**
     *
     */
    fun readAirportDataFile(input: InputStream): List<Airport> {
        val airports = mutableListOf<Airport>()
        var string: String? = ""
        val reader = BufferedReader(InputStreamReader(`input`))
        while (true) {
            try {
                if (reader.readLine().also { string = it } == null) break
            }
            catch (e: IOException) {
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
                airports.add(airport)
            }
        }
        `input`.close()

        return airports
    }

    fun setAirportDeparture(airport: Airport) {
        latitudeFrom = airport.latitude
        longitudeFrom = airport.longitude
        Log.d(TAG, "setting departure airport latitude ($latitudeFrom) & longitude ($longitudeFrom)")
    }

    fun setAirportArrival(airport: Airport) {
        latitudeTo = airport.latitude
        longitudeTo = airport.longitude
        Log.d(TAG, "setting departure airport latitude ($latitudeTo) & longitude ($longitudeTo)")
    }

    /**
     *
     */
    fun calculateDistanceBetweenAirports() {
        val results = FloatArray(1)
        Location.distanceBetween(latitudeFrom, longitudeFrom, latitudeTo, longitudeTo, results)
        _distanceValueKm.value = (results[0] / 1000).roundToInt()
        Log.d(TAG, "distance between the 2 airports : ${_distanceValueKm.value} kilometers")
    }

    fun clearTotalDistance() {
        Log.d(TAG, "clearing total distance in kms")
        _distanceValueKm.value = 0
    }
}