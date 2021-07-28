package cga.exercise.game.level

import kotlin.math.ln
import kotlin.math.max

class Scoring {
    val maxMultiplier = 8
    fun score(v: Float){
        points += multiplier * v
        combo++
        if (combo > maxCombo)
            maxCombo = combo
        multiplier = max((ln(combo.toFloat())+1).toInt(), maxMultiplier)
        println("scored! $points")
    }

    fun fail() {
        combo = 0
        multiplier = 1
    }

    var points =  0f
    var maxCombo = 0
    private var combo: Int = 0
    private var multiplier: Int = 1

}