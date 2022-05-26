package com.idplus.flyco2tracker

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.idplus.flyco2tracker.databinding.FragmentResultBinding
import com.idplus.flyco2tracker.databinding.FragmentSimulationBinding
import kotlin.math.roundToInt


class ResultFragment : Fragment() {

    private var TAG: String = "ResultFragment"
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val RADIATIVE_FORCING_COEFF_CONSERVATIVE = 2
    private val RADIATIVE_FORCING_COEFF_RECENT = 3
    private val CARBON_BUDGET_MAX = 2000


    override fun onCreateView(inf: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentResultBinding.inflate(inf, container, false)
        val view = binding.root

        // receive the arguments from the previous fragment using safe args plugin
        val distance = ResultFragmentArgs.fromBundle(requireArguments()).totalDistance
        val returnTrip = ResultFragmentArgs.fromBundle(requireArguments()).returnTrip
        val comfortCategory = ResultFragmentArgs.fromBundle(requireArguments()).comfortCategory

        // calculate the total greenhouse gas total emissions in kgs
        var totalGhg =
            distance * (if(returnTrip) 2 else 1) *              // calculate total distance according if it's a return trip or not
            getUnitaryCarbonImpact(distance) *                  // calculate unitary carbon impact according to the total distance of the trip
            getRadiativeForcingCoefficient(false) *   // get the selected radiative forcing coefficient (conservative or most recent)
            getMalusComfort(distance, comfortCategory)          // get the malus coefficient according to the comfort category

        var totalGhgRounded = totalGhg.roundToInt()

        // calculate the percentage off the annual carbon budget required to respect the Paris Accords
        var carbonBudgetPercentage = (( totalGhg / CARBON_BUDGET_MAX ) * 100).roundToInt()

        // display the carbon budget percentage on the UI
        binding.valuePercentageBudget.text = "$carbonBudgetPercentage %"

        // pick the right color to visualize the impact of the carbon footprint result
        var colorBudget: Int = Color.parseColor("#83621f")
        if(carbonBudgetPercentage > 50 && carbonBudgetPercentage <= 100)
            colorBudget = Color.parseColor("#531919")
        else if(carbonBudgetPercentage >= 100)
            colorBudget = Color.parseColor("#191919")

        binding.layoutAnnualCarbonBudget.setBackgroundColor(colorBudget)

        // display the total greenhouse gas total emissions of the selected flight in kgs of CO2 equivalent
        binding.labelTotalGes.text = "$totalGhgRounded kg CO2e"

        return view
    }


    private fun getMalusComfort(distanceOneWay: Int, comfortCategory: String): Double {
        var malus = 0.0

        // if comfort category is normal (economy/2nd class)
        if(comfortCategory.equals("normal"))
            malus = if(distanceOneWay < 1500) 0.96 else 0.8

        // if comfort category is (business/1st classe)
        else if(comfortCategory.equals("business"))
            malus = if(distanceOneWay < 1500) 1.26 else 1.54

        // if comfort category is VIP (super comfort, whatever)
        else if(comfortCategory.equals("comfort"))
            malus = 2.4

        return malus
    }

    private fun getRadiativeForcingCoefficient(recentCoeff: Boolean): Int {
        return if(recentCoeff) RADIATIVE_FORCING_COEFF_RECENT else RADIATIVE_FORCING_COEFF_CONSERVATIVE
    }

    private fun getUnitaryCarbonImpact(distanceOneWay: Int): Double {
        var impact: Double

        if (distanceOneWay < 500)
            impact = (0.161 + 0.181) / 2
        else if (distanceOneWay >= 500 && distanceOneWay < 1000)
            impact = 0.134
        else if (distanceOneWay >= 1000 && distanceOneWay < 2000)
            impact = 0.106
        else if (distanceOneWay >= 2000 && distanceOneWay < 5000)
            impact = 0.098
        else
            impact = 0.083

        return impact
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}