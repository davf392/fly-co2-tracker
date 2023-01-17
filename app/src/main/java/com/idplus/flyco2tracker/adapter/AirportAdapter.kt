package com.idplus.flyco2tracker.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.idplus.flyco2tracker.R
import com.idplus.flyco2tracker.data.Airport
import java.text.Normalizer
import java.text.Normalizer.normalize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min


class AirportAdapter(private val mContext: Context, private val mLayoutResId: Int, airports: List<Airport>) :
    ArrayAdapter<Airport>(mContext, mLayoutResId, airports) {

    private val TAG = AirportAdapter::class.java.toString()

    private var airport: MutableList<Airport> = ArrayList(airports)
    private var allAirports: List<Airport> = ArrayList(airports)

    private var userConstraint: String = ""

    override fun getCount() = airport.size

    override fun getItem(position: Int) = airport[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = super.getView(position, convertView, parent)
        var convertView = convertView
        if (convertView == null) {
            val inflater = (mContext as Activity).layoutInflater
            convertView = inflater.inflate(mLayoutResId, parent, false)
        }

        try {
            val airportAutoCompleteItemName: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(
                R.id.airport_name, parent, false) as TextView
            val airport: Airport = getItem(position)

            airportAutoCompleteItemName.text = airport.name

            if(userConstraint.isNotEmpty())
                airportAutoCompleteItemName.text = highlight(userConstraint, airport.toString())
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        return convertView!!
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun convertResultToString(resultValue: Any) = (resultValue as Airport).name

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val airportSuggestion: MutableList<Airport> = ArrayList()
                    for (airport in allAirports) {
                        if (airport.name.lowercase().startsWith(constraint.toString().lowercase()) ||
                            airport.country.lowercase().startsWith(constraint.toString().lowercase()) ||
                            airport.city.lowercase().startsWith(constraint.toString().lowercase())) {

                            airportSuggestion.add(airport)
                        }
                    }
                    filterResults.values = airportSuggestion
                    filterResults.count = airportSuggestion.size
                    userConstraint = constraint.toString()
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                airport.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is Airport) {
                            airport.add(result)
                        }
                    }
                    notifyDataSetChanged()
                }
                else if (constraint == null) {
                    airport.addAll(allAirports)
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    fun highlight(search: String, originalText: String): CharSequence {
        // ignore case and accents
        // the same thing should have been done for the search text
        val normalizedText: String = normalize(originalText, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+", "")
            .lowercase(Locale.ENGLISH);

        var start: Int = normalizedText.indexOf(search.lowercase(Locale.ENGLISH))
        if (start < 0) {
            // not found, nothing to to
            return originalText;
        }
        else {
            // highlight each appearance in the original text
            // while searching in normalized text
            val highlighted: Spannable = SpannableString(originalText)
            while (start >= 0) {
                val spanStart: Int = min(start, originalText.length)
                val spanEnd: Int = min(start + search.length, originalText.length)

                highlighted.setSpan(
                    ForegroundColorSpan(Color.BLUE),
                    spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }
}
