package com.idplus.flyco2tracker.simulation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.idplus.flyco2tracker.Constants.Companion.COMFORT_CATEGORY_BUSINESS
import com.idplus.flyco2tracker.Constants.Companion.COMFORT_CATEGORY_EXTRA
import com.idplus.flyco2tracker.Constants.Companion.COMFORT_CATEGORY_NORMAL
import com.idplus.flyco2tracker.model.Airport
import com.idplus.flyco2tracker.adapter.AirportAdapter
import com.idplus.flyco2tracker.R
import com.idplus.flyco2tracker.databinding.FragmentSimulationBinding

const val TAF = "SimulationFragment"

class SimulationFragment : Fragment() {

    private lateinit var navController: NavController
    lateinit var viewModel: SimulationViewModel
    private lateinit var binding: FragmentSimulationBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentBinding = FragmentSimulationBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        viewModel = ViewModelProvider(this).get(SimulationViewModel::class.java)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.apply {
            simulationViewModel = viewModel
            fragment = this@SimulationFragment
            lifecycleOwner = viewLifecycleOwner
        }

        // load into memory the list of airport information (if not already done)
        viewModel.readAirportDataFile(resources.openRawResource(R.raw.airlines))

        setupAirportSearchAdapter()
        setupClearAirportButtons(binding.autoCompleteAirportFrom, binding.btnClearAirportFrom)
        setupClearAirportButtons(binding.autoCompleteAirportTo, binding.btnClearAirportTo)
        setupAutoCompleteClickListeners()
    }

    /**
     *
     */
    private fun setupAutoCompleteClickListeners() {
        // define the onclick listener for the departure airport selection
        binding.autoCompleteAirportFrom.setOnItemClickListener { adapterView, _, i, _ ->
            // and we calculate the distance between this airport & the arrival airport if available
            viewModel.setAirportDeparture(adapterView.getItemAtPosition(i) as Airport)
            binding.autoCompleteAirportFrom.setSelection(0)
        }

        // define the onclick listener for the departure airport selection
        binding.autoCompleteAirportTo.setOnItemClickListener { adapterView, _, i, _ ->
            // we get the airport position & calculate the distance between this airport & the departure airport if available
            viewModel.setAirportArrival(adapterView.getItemAtPosition(i) as Airport)
            binding.autoCompleteAirportTo.setSelection(0)
        }
    }

    /**
     *
     */
    private fun setupClearAirportButtons(autoCompleteTextView: AutoCompleteTextView, btnClear: Button) {
        // make clear button appear or disappear depending whether the text view is empty or not
        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s!!.isNotEmpty() -> { btnClear.visibility = View.VISIBLE }
                    else -> { btnClear.visibility = View.GONE }
                }
            }
        })

        btnClear.setOnClickListener {
            // we clear the text and make the clear button disappear
            autoCompleteTextView.setText("")
            btnClear.visibility = View.GONE
            viewModel.clearTotalDistance()   // we also set the distance to 0 km
        }
    }

    /**
     *
     */
    private fun setupAirportSearchAdapter() {
        context?.let { ctx ->
            val airportAdapter = AirportAdapter(ctx, R.layout.airport_item, viewModel.airportList)
            binding.autoCompleteAirportFrom.setAdapter(airportAdapter)
            binding.autoCompleteAirportFrom.setOnItemClickListener { _, _, position, _ ->
                binding.autoCompleteAirportFrom.setText(airportAdapter.getItem(position).name)
            }

            binding.autoCompleteAirportTo.setAdapter(airportAdapter)
            binding.autoCompleteAirportTo.setOnItemClickListener { _, _, position, _ ->
                binding.autoCompleteAirportTo.setText(airportAdapter.getItem(position).name)
            }
        }
    }

    /**
     *
     */
    fun saveUserInputAndComputeFootprint() {

        viewModel.setFlightPreferences(
            binding.toggleGrpReturnFlight.checkedButtonId == R.id.btn_return,
            when(binding.toggleGrpComfortClass.checkedButtonId) {
                R.id.btn_class_economy -> COMFORT_CATEGORY_NORMAL
                R.id.btn_class_business -> COMFORT_CATEGORY_BUSINESS
                R.id.btn_class_comfort -> COMFORT_CATEGORY_EXTRA
                else -> COMFORT_CATEGORY_NORMAL
            }
        )

        // then we ask the navigation controller to redirect the user to the com.idplus.flyco2tracker.result fragment
        navController.navigate(
            SimulationFragmentDirections.actionSimulationFragmentToResultFragment(
                viewModel.distanceValueKm.value!!,  // the total distance to take into account
                viewModel.isReturnTrip,   // is it a return trip or not
                viewModel.comfortValue    // carbon footprint also depends on travel comfort category
            )
        )
    }
}