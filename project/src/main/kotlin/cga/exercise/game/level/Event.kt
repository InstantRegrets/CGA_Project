package cga.exercise.game.level

import chart.difficulty._events

class Event (
    val beat: Float,
    val light: Type,
    val effect: Effect,
){

    enum class Type(val _type: Int){
        BackLasers(0),
        RingLights(1),
        LeftRotatingLasers(2),
        RightRotationgLasers(3),
        CenterLights(5),
    }

    enum class Effect(val _value: Int){
        LightOff(0),
        LightOnBlue(1),
        LightFlashBlue(2),
        LightFadeBlue(3),
        LightOnRed(5),
        LightFlashRed(6),
        LightFadeRed(7),
    }
}