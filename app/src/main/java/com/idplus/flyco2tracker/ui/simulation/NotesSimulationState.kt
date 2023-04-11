package com.idplus.flyco2tracker.ui.simulation

import com.idplus.flyco2tracker.ui.adapter.Airport

data class NotesSimulationState(
    val airportSuggestions: List<Airport> = emptyList(),
    val listDestinationAirportDropDownExpanded: Boolean = false,
    val listArrivalAirportDropDownExpanded: Boolean = false,
    val selectedDestinationAirport: Airport = Airport(),
    val selectedArrivalAirport: Airport = Airport(),
    val isClearDestinationAirportVisible: Boolean = false,
    val isClearArrivalAirportVisible: Boolean = false,
    val urlDestinationImage: String? = null
)
