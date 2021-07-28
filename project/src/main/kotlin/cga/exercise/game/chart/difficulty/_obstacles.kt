@file:Suppress("ClassName")

package chart.difficulty

import com.google.gson.annotations.SerializedName

data class _obstacles (
    var _time : Double,
    val _lineIndex : Int,
    val _type : Int,
    var _duration : Double,
    val _width : Int,
    val _customData : _obstacleCustomData?
)
