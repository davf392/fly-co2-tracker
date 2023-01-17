package com.idplus.flyco2tracker.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt


class ResultViewModel(distance: Int, returnTrip: Boolean, comfort: String): ViewModel() {

    val TAG = ResultViewModel::class.java.toString()

    private val RADIATIVE_FORCING_COEFF_CONSERVATIVE = 2
    private val RADIATIVE_FORCING_COEFF_RECENT = 3
    val CARBON_BUDGET_MAX = 2000

    private var _targetCarbonBudget = MutableLiveData(0)
    val targetCarbonBudget: LiveData<Int> get() = _targetCarbonBudget

    private val _footprintPlane = MutableLiveData(0)
    val footprintPlane: LiveData<Int> get() = _footprintPlane

    private val _percentageOffBudget = MutableLiveData(0)
    val percentageOffBudget: LiveData<Int> get() = _percentageOffBudget

    private var _footprintCar = MutableLiveData(0)
    val footprintCar: LiveData<Int> get() = _footprintCar

    private var _footprintAutobus = MutableLiveData(0)
    val footprintAutobus: LiveData<Int> get() = _footprintAutobus

    private var _footprintTrain = MutableLiveData(0)
    val footprintTrain: LiveData<Int> get() = _footprintTrain

    var totalDistance = distance
    var isReturnTrip = returnTrip
    var comfortCategory = comfort


    init {
        // calculate the total greenhouse gas total emissions in kgs
        val totalGhg =
            totalDistance * (if(isReturnTrip) 2 else 1) *              // calculate total distance according if it's a return trip or not
            getUnitaryCarbonImpact(totalDistance) *                 // calculate unitary carbon impact according to the total distance of the trip
            getRadiativeForcingCoefficient(false) *         // get the selected radiative forcing coefficient (conservative or most recent)
            getMalusComfort(totalDistance, comfortCategory)          // get the malus coefficient according to the comfort category

        _footprintPlane.value = totalGhg.roundToInt()

        _footprintTrain.value = (0.002 * totalDistance).roundToInt()
        _footprintAutobus.value = (0.1 * totalDistance).roundToInt()
        _footprintCar.value = (0.2 * totalDistance).roundToInt()
        _targetCarbonBudget.value = CARBON_BUDGET_MAX

        // calculate the total budget percentage and display on the UI using live data
        _percentageOffBudget.value = (( totalGhg / CARBON_BUDGET_MAX ) * 100).roundToInt()
    }

    private fun getMalusComfort(distanceOneWay: Int, comfort: String): Double {
        var malus = 0.0

        // if comfort category is normal (economy/2nd class)
        if(comfort == "normal")
            malus = if(distanceOneWay < 1500) 0.96 else 0.8

        // if comfort category is (business/1st class)
        else if(comfort == "business")
            malus = if(distanceOneWay < 1500) 1.26 else 1.54

        // if comfort category is VIP (super comfort, whatever)
        else if(comfort == "comfort")
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
        else if (distanceOneWay in 500..999)
            impact = 0.134
        else if (distanceOneWay in 1000..1999)
            impact = 0.106
        else if (distanceOneWay in 2000..4999)
            impact = 0.098
        else
            impact = 0.083

        return impact
    }

}