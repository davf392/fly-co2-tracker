package com.idplus.flyco2tracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.idplus.flyco2tracker.databinding.FragmentResultBinding


class ResultFragment : Fragment() {

    private val TAG: String = ResultFragment::class.java.toString()

    lateinit var resultViewModel: ResultViewModel
    lateinit var viewModelFactory: ResultViewModelFactory

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inf: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentResultBinding.inflate(inf, container, false)
        val view = binding.root

        // get the arguments from the previous screen (SimulationFragment)
        val result = ResultFragmentArgs.fromBundle(requireArguments())

        // create the associated view model using the factory
        viewModelFactory = ResultViewModelFactory(result.totalDistance, result.returnTrip, result.comfortCategory)
        resultViewModel = ViewModelProvider(this, viewModelFactory).get(ResultViewModel::class.java)

        // we assign the layout's view model created by data binding to the ResultViewModel property
        binding.resultViewModel = resultViewModel

        // get the background color for the total budget percentage layout
        binding.layoutAnnualCarbonBudget.setBackgroundColor(resultViewModel.getResultLayoutColor())

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}