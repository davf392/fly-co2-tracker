package com.idplus.flyco2tracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class FootprintResultFragment : Fragment() {

    override fun onCreateView(inf: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inf.inflate(R.layout.fragment_footprint_result, container, false)
    }
}