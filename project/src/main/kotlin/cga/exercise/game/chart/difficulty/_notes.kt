package chart.difficulty

import com.google.gson.annotations.SerializedName

data class _notes (

    var _time : Double,
    val _lineIndex : Int,
    val _lineLayer : Int,
    val _type : Int,
    val _cutDirection : Int,
    val _customData : _noteCustomData? = null
)

