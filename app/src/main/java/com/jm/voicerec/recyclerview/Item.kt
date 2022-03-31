package com.jm.voicerec.recyclerview

import androidx.annotation.DrawableRes

data class Item(
    var content : String,
    @DrawableRes val icon: Int
//    val recordBtn : ToggleButton
)
