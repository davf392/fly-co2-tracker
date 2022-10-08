package com.idplus.flyco2tracker

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

class ResultViewModel(distance: Int, returnTrip: Boolean, comfort: String): ViewModel() {

    val TAG = ResultViewModel::class.java.toString()

    private val RADIATIVE_FORCING_COEFF_CONSERVATIVE = 2
    private val RADIATIVE_FORCING_COEFF_RECENT = 3
    private val CARBON_BUDGET_MAX = 2000

    private val _totalCarbon = MutableLiveData(0)
    val totalCarbon: LiveData<Int> get() = _totalCarbon

    private val _percentageOffBudget = MutableLiveData(0)
    val percentageOffBudget: LiveData<Int> get() = _percentageOffBudget

    var totalDistance = distance
    var isReturnTrip = returnTrip
    var comfortCategory = comfort


    init {
        // calculate the total greenhouse gas total emissions in kgs
        var totalGhg =

            totalDistance * (if(isReturnTrip) 2 else 1) *              // calculate total distance according if it's a return trip or not

            getUnitaryCarbonImpact(totalDistance) *                 // calculate unitary carbon impact according to the total distance of the trip

            getRadiativeForcingCoefficient(false) *         // get the selected radiative forcing coefficient (conservative or most recent)

            getMalusComfort(totalDistance, comfortCategory)          // get the malus coefficient according to the comfort category

        _totalCarbon.value = totalGhg.roundToInt()

        // calculate the total budget percentage and display on the UI using live data
        _percentageOffBudget.value = (( totalGhg / CARBON_BUDGET_MAX ) * 100).roundToInt()
    }

    private fun getMalusComfort(distanceOneWay: Int, comfort: String): Double {
        var malus = 0.0

        // if comfort category is normal (economy/2nd class)
        if(comfort.equals("normal"))
            malus = if(distanceOneWay < 1500) 0.96 else 0.8

        // if comfort category is (business/1st classe)
        else if(comfort.equals("business"))
            malus = if(distanceOneWay < 1500) 1.26 else 1.54

        // if comfort category is VIP (super comfort, whatever)
        else if(comfort.equals("comfort"))
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

    fun getResultLayoutColor(): Int {
        // pick the right color to visualize the impact of the carbon footprint result
        var colorBudget = Color.parseColor("#e1d738")

        if(_percentageOffBudget.value!! in 20..30)
            colorBudget = Color.parseColor("#efc33a")
        else if(_percentageOffBudget.value!! in 30..40)
            colorBudget = Color.parseColor("#f97b27")
        else if(_percentageOffBudget.value!! in 40..50)
            colorBudget = Color.parseColor("#fc3e14")
        else if(_percentageOffBudget.value!! in 50..60)
            colorBudget = Color.parseColor("#ff0000")
        else if(_percentageOffBudget.value!! in 60..70)
            colorBudget = Color.parseColor("#aa0000")
        else if(_percentageOffBudget.value!! in 70..80)
            colorBudget = Color.parseColor("#850000")
        else if(_percentageOffBudget.value!! >= 80)
            colorBudget = Color.parseColor("#000000")

        return colorBudget
    }
}