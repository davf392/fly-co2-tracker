package com.idplus.flyco2tracker

class Airport {
    var name: String = ""
    var city: String = ""
    var country: String = ""
    var iata: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    object ParsingKeys {
        const val AIRPORT_NAME_ID   : Int = 1
        const val CITY_NAME_ID      : Int = 2
        const val COUNTRY_NAME_ID   : Int = 3
        const val COUNTRY_IATA_ID   : Int = 4
        const val LATITUDE_ID       : Int = 6
        const val LONGITUDE_ID      : Int = 7
    }

    override fun toString(): String {
        return "$name, $country ($iata)"
    }
}
