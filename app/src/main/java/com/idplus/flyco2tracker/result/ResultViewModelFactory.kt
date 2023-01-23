package com.idplus.flyco2tracker.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ResultViewModelFactory(final val distance: Int, final val returnTrip: Boolean, val comfort: String): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ResultViewModel::class.java))
            return ResultViewModel(distance, returnTrip, comfort) as T

        return super.create(modelClass)
    }

}