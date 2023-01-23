package com.idplus.flyco2tracker.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.idplus.flyco2tracker.databinding.FragmentResultBinding

const val TAG = "ResultFragment"

class ResultFragment : Fragment() {

    lateinit var resultViewModel: ResultViewModel
    lateinit var viewModelFactory: ResultViewModelFactory
    private lateinit var binding: FragmentResultBinding

    override fun onCreateView(inf: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        val fragmentBinding = FragmentResultBinding.inflate(inf, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get the arguments from the previous screen (SimulationFragment)
        val result = ResultFragmentArgs.fromBundle(requireArguments())

        // create the associated view model using the factory
        viewModelFactory = ResultViewModelFactory(result.totalDistance, result.returnTrip, result.comfortCategory)
        resultViewModel = ViewModelProvider(this, viewModelFactory).get(ResultViewModel::class.java)

        binding.apply {
            resultModel = resultViewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }
}