package com.example.nasapictureoftheday

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel: ViewModel() {
    var currentDay = MutableLiveData<APIFormat>(APIFormat("","","","",""))

    fun updateDay(newDay:APIFormat) {
        currentDay.value = newDay
    }
}