package com.idplus.flyco2tracker

import android.content.Context
import android.location.Location.distanceBetween
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.idplus.flyco2tracker.Airport.ParsingKeys.AIRPORT_NAME_ID
import com.idplus.flyco2tracker.Airport.ParsingKeys.CITY_NAME_ID
import com.idplus.flyco2tracker.Airport.ParsingKeys.COUNTRY_IATA_ID
import com.idplus.flyco2tracker.Airport.ParsingKeys.COUNTRY_NAME_ID
import com.idplus.flyco2tracker.Airport.ParsingKeys.LATITUDE_ID
import com.idplus.flyco2tracker.Airport.ParsingKeys.LONGITUDE_ID
import com.idplus.flyco2tracker.databinding.FragmentSimulationBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.roundToInt

class SimulationFragment : Fragment() {

    private var TAG: String = "SimulationFragment"
    private var _binding: FragmentSimulationBinding? = null
    private val binding get() = _binding!!

    private var _totalDistance: Int = 0
    private var _latitudeFrom: Double = 0.0
    private var _longitudeFrom: Double = 0.0
    private var _latitudeTo: Double = 0.0
    private var _longitudeTo: Double = 0.0

    override fun onCreateView(inf: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSimulationBinding.inflate(inf, container, false)
        val view = binding.root

        // load into memory the list of airport information (iata, country, city, etc)
        val airportList = readAirportDataFile(R.raw.airlines)

        // make the "clear" buttons for each text view disappear
        binding.btnClearAirportFrom.visibility = View.GONE
        binding.btnClearAirportTo.visibility = View.GONE

        // set default adapter for both AutoCompleteTextView (from & to)
        val adapter = ArrayAdapter(activity as Context, android.R.layout.simple_list_item_1, airportList)
        binding.autoCompleteAirportFrom.setAdapter(adapter)
        binding.autoCompleteAirportTo.setAdapter(adapter)

        // make clear button appear or disappear depending whether the text view is empty or not
        binding.autoCompleteAirportFrom.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s!!.isNotEmpty() -> { binding.btnClearAirportFrom.visibility = View.VISIBLE }
                    else -> { binding.btnClearAirportFrom.visibility = View.GONE }
                }
            }
        })

        // make clear button appear or disappear depending whether the text view is empty or not
        binding.autoCompleteAirportTo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s!!.isNotEmpty() -> { binding.btnClearAirportTo.visibility = View.VISIBLE }
                    else -> { binding.btnClearAirportTo.visibility = View.GONE }
                }
            }
        })

        // whenever we select a departure airport from the available list
        binding.autoCompleteAirportFrom.setOnItemClickListener { adapterView, view, i, l ->
            // we get the geographic position (lat/long)
            val airport: Airport = adapterView.getItemAtPosition(i) as Airport
            _latitudeFrom = airport.latitude
            _longitudeFrom = airport.longitude

            // and we calculate the distance between this airport & the arrival airport if available
            _totalDistance = calculateDistanceBetweenAirports()
        }

        // whenever we select a arrival airport from the available list
        binding.autoCompleteAirportTo.setOnItemClickListener { adapterView, view, i, l ->
            // we get the geographic position (lat/long)
            val airport: Airport = adapterView.getItemAtPosition(i) as Airport
            _latitudeTo = airport.latitude
            _longitudeTo = airport.longitude

            // and we calculate the distance between this airport & the departure airport if available
            _totalDistance = calculateDistanceBetweenAirports()
        }

        // whenever the clear button is clicked for the arrival airport
        binding.btnClearAirportTo.setOnClickListener {
            // we clear the text and make the clear button disappear
            binding.autoCompleteAirportTo.setText("")
            binding.btnClearAirportTo.visibility = View.GONE
            binding.distanceValueKm.text = "0 km"   // we also set the distance to 0 km
        }

        // whenever the clear button is clicked for the departure airport
        binding.btnClearAirportFrom.setOnClickListener {
            // we clear the text and make the clear button disappear
            binding.autoCompleteAirportFrom.setText("")
            binding.btnClearAirportFrom.visibility = View.GONE
            binding.distanceValueKm.text = "0 km"   // we also set the distance to 0 km
        }

        // whenever the Calculate button is pressed, we send the parameters entered by the user
        binding.btnComputeFootprint.setOnClickListener {
            // get total distance -> _totalDistance

            // is it one way travel ? or return trip ?
            var returnTrip = false
            if( binding.toggleGrpReturnFlight.checkedButtonId == R.id.btn_return)
                returnTrip = true

            // set comfort value by default to O (economy)
            var comfort = "normal"
            if(binding.toggleGrpComfortClass.checkedButtonId == R.id.btn_class_business)
                comfort = "business"
            else if(binding.toggleGrpComfortClass.checkedButtonId == R.id.btn_class_comfort)
                comfort = "comfort"

            // send parameters to next fragment then switch display
            Log.d(TAG, "flight carbon footprint ready to be calculated")

            // then we ask the navigation controller to redirect the user to the result fragment
            val action = SimulationFragmentDirections.actionSimulationFragmentToResultFragment(_totalDistance, returnTrip, comfort)
            view.findNavController().navigate(action)
        }

        return view
    }

    /**
     *
     */
    fun calculateDistanceBetweenAirports(): Int {
        val results = FloatArray(1)
        distanceBetween(_latitudeFrom, _longitudeFrom, _latitudeTo, _longitudeTo, results)
        _totalDistance = (results[0] / 1000).roundToInt()
        Log.d(TAG, "distance between the 2 airports : $_totalDistance meters")
        binding.distanceValueKm.text = "$_totalDistance km"

        return _totalDistance
    }

    /**
     *
     */
    fun readAirportDataFile(resId: Int): List<Airport> {
        val airports = mutableListOf<Airport>()
        var string: String? = ""
        val `is`: InputStream = this.resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(`is`))
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
                airport.name = airportInfo[AIRPORT_NAME_ID]
                airport.city = airportInfo[CITY_NAME_ID]
                airport.country = airportInfo[COUNTRY_NAME_ID]
                airport.iata = airportInfo[COUNTRY_IATA_ID]
                airport.latitude = airportInfo[LATITUDE_ID].toDoubleOrNull()!!
                airport.longitude = airportInfo[LONGITUDE_ID].toDoubleOrNull()!!
                airports.add(airport)
            }
        }
        `is`.close()

        return airports
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}