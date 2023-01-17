package com.idplus.flyco2tracker.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.idplus.flyco2tracker.model.ResultViewModelFactory
import com.idplus.flyco2tracker.databinding.FragmentResultBinding
import com.idplus.flyco2tracker.model.ResultViewModel


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

        binding.resultModel = resultViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}