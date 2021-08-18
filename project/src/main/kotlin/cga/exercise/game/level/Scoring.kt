package cga.exercise.game.level

import cga.exercise.components.sound.SoundBuffer
import cga.exercise.components.sound.SoundSource
import java.io.File
import kotlin.math.ln
import kotlin.math.max

class Scoring {
    private val hitsounds = arrayOf(
        SoundSource(false,false, SoundBuffer(File("assets/sound/hit-1.ogg"))),
        SoundSource(false,false, SoundBuffer(File("assets/sound/hit-2.ogg"))),
        SoundSource(false,false, SoundBuffer(File("assets/sound/hit-3.ogg"))),
        SoundSource(false,false, SoundBuffer(File("assets/sound/hit-4.ogg"))),
        SoundSource(false,false, SoundBuffer(File("assets/sound/hit-5.ogg"))),
        SoundSource(false,false, SoundBuffer(File("assets/sound/hit-6.ogg"))),
        SoundSource(false,false, SoundBuffer(File("assets/sound/hit-7.ogg"))),
        SoundSource(false,false, SoundBuffer(File("assets/sound/hit-8.ogg"))),
    )

    val maxMultiplier = 8
    fun score(v: Float){
        hitsounds.random().play()
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