package com.idplus.flyco2tracker.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.idplus.flyco2tracker.data.Airport
import com.idplus.flyco2tracker.adapter.AirportAdapter
import com.idplus.flyco2tracker.R
import com.idplus.flyco2tracker.databinding.FragmentSimulationBinding
import com.idplus.flyco2tracker.model.SimulationViewModel


class SimulationFragment : Fragment() {

    private var TAG: String = SimulationFragment::class.java.toString()

    private lateinit var airportList: List<Airport>
    lateinit var viewModel: SimulationViewModel

    private var _binding: FragmentSimulationBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inf: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSimulationBinding.inflate(inf, container, false)
        val view = binding.root

        // get view model instance & assign the layout's data binding variable to the view model property
        viewModel = ViewModelProvider(this).get(SimulationViewModel::class.java)
        binding.simulationViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner     // let the layout respond to live data updates

        // make the "clear" buttons for each text view disappear
        binding.btnClearAirportFrom.visibility = View.GONE
        binding.btnClearAirportTo.visibility = View.GONE

        // load into memory the list of airport information (if not already done)
        if(!viewModel.airportDataLoaded)
            airportList = viewModel.readAirportDataFile(this.resources.openRawResource(R.raw.airlines))

        // and set default adapter for both AutoCompleteTextView (from & to)
        context?.let { ctx ->
            val airportAdapter = AirportAdapter(ctx, R.layout.airport_item, airportList)
            binding.autoCompleteAirportFrom.setAdapter(airportAdapter)
            binding.autoCompleteAirportFrom.setOnItemClickListener { parent, _, position, _ ->
                val city = airportAdapter.getItem(position) as Airport
                binding.autoCompleteAirportFrom.setText(city.name)
            }

            // hide keyboard upon scrolling down the results in the drop down list
//            binding.autoCompleteAirportFrom.setOnTouchListener { view, motionEvent ->
//
//                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                manager.hideSoftInputFromWindow(view.windowToken, 0)
//            }

            binding.autoCompleteAirportTo.setAdapter(airportAdapter)
            binding.autoCompleteAirportTo.setOnItemClickListener { parent, _, position, _ ->
                val city = airportAdapter.getItem(position) as Airport
                binding.autoCompleteAirportTo.setText(city.name)
            }
        }

        // make clear button appear or disappear depending whether the text view is empty or not
        binding.autoCompleteAirportFrom.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s!!.isNotEmpty() -> {
                        binding.btnClearAirportFrom.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.btnClearAirportFrom.visibility = View.GONE
                    }
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

        // DEPARTURE AIRPORT ON CLICK
        binding.autoCompleteAirportFrom.setOnItemClickListener { adapterView, view, i, l ->
            // and we calculate the distance between this airport & the arrival airport if available
            viewModel.setAirportDeparture(adapterView.getItemAtPosition(i) as Airport)
            viewModel.calculateDistanceBetweenAirports()
            binding.autoCompleteAirportFrom.setSelection(0)
        }

        // ARRIVAL AIRPORT ON CLICK
        binding.autoCompleteAirportTo.setOnItemClickListener { adapterView, view, i, l ->
            // we get the airport position & calculate the distance between this airport & the departure airport if available
            viewModel.setAirportArrival(adapterView.getItemAtPosition(i) as Airport)
            viewModel.calculateDistanceBetweenAirports()
            binding.autoCompleteAirportTo.setSelection(0)
        }

        // CLEAR DEPARTURE AIRPORT ON CLICK
        binding.btnClearAirportFrom.setOnClickListener {
            // we clear the text and make the clear button disappear
            binding.autoCompleteAirportFrom.setText("")
            binding.btnClearAirportFrom.visibility = View.GONE
            viewModel.clearTotalDistance()   // we also set the distance to 0 km
        }

        // CLEAR ARRIVAL AIRPORT ON CLICK
        binding.btnClearAirportTo.setOnClickListener {
            // we clear the text and make the clear button disappear
            binding.autoCompleteAirportTo.setText("")
            binding.btnClearAirportTo.visibility = View.GONE
            viewModel.clearTotalDistance()   // we also set the distance to 0 km
        }

        // CALCULATE CARBON FOOTPRINT ON CLICK
        binding.btnComputeFootprint.setOnClickListener {

            // is it one way travel ? or return trip ?
            viewModel.isReturnTrip = (binding.toggleGrpReturnFlight.checkedButtonId == R.id.btn_return)

            // set comfort value by default to O (economy)
            viewModel.comfortValue = "normal"
            if(binding.toggleGrpComfortClass.checkedButtonId == R.id.btn_class_business)
                viewModel.comfortValue = "business"
            else if(binding.toggleGrpComfortClass.checkedButtonId == R.id.btn_class_comfort)
                viewModel.comfortValue = "comfort"

            // send parameters to next fragment then switch display
            Log.d(TAG, "flight carbon footprint ready to be calculated")

            // then we ask the navigation controller to redirect the user to the result fragment
            view.findNavController().navigate(
                SimulationFragmentDirections.actionSimulationFragmentToResultFragment(
                    viewModel.distanceValueKm.value!!,  // the total distance to take into account
                    viewModel.isReturnTrip,   // is it a return trip or not
                    viewModel.comfortValue    // carbon footprint also depends on travel comfort category
                )
            )
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}