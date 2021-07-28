@file:Suppress("ClassName")

package chart.info

import com.google.gson.annotations.SerializedName

data class _difficultyBeatmaps(

    val _difficulty: String,
    val _difficultyRank: Int,
    val _beatmapFilename: String,

    val _noteJumpMovementSpeed: Double,
    val _noteJumpStartBeatOffset: Double,

    val _customData: _customData?

)