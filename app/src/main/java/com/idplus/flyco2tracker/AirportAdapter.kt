package com.idplus.flyco2tracker

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class AirportAdapter(private val _context: Context, private val _resId: Int, airports: List<Airport>) : ArrayAdapter<Airport>(_context, _resId, airports) {

    private val city: MutableList<Airport> = ArrayList(airports)

    override fun getCount(): Int {
        return city.size
    }
    override fun getItem(position: Int): Airport {
        return city[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (_context as Activity).layoutInflater
            convertView = inflater.inflate(_resId, parent, false)
        }
        try {
//            val airport: Airport = getItem(position)
//            val airportAutoCompleteView = convertView!!.findViewById<View>(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item) as TextView
//            airportAutoCompleteView.text = airport.toString()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }
}
