package cga.exercise.game

import java.util.concurrent.TimeUnit

class FPSLogger{
    //Framerate calculation
    val printOut = true
    private var startTime = System.nanoTime()
    private var timer: Long = 0L
    private var frames = 0
    private var geometryTime = 0L
    private var ambientTime = 0L
    private var spotLightTime = 0L
    private var pointLightTime = 0L
    private var finalPassTime = 0L
    private var skyboxTime = 0L
    fun logFps(){
        frames++
        if(System.nanoTime() - startTime >= 1000000000) {
            if (printOut){
                println("=======================")
                println("FPSCounter: fps $frames")
                println("avg geometry Pass:   ${geometryTime    /frames}")
                println("avg ambient Pass:    ${ambientTime     /frames}")
                println("avg spotlight Pass:  ${spotLightTime   /frames}")
                println("avg pointLight Pass: ${pointLightTime  /frames}")
                println("avg final PassTime:  ${finalPassTime   /frames}")
                println("avg skybox PassTime: ${skyboxTime      /frames}")
                println("avg total render ms: ${(geometryTime + ambientTime + spotLightTime + pointLightTime + finalPassTime  + skyboxTime)/frames.toFloat()/1000000f}")
            }

            frames = 0
            startTime = System.nanoTime()
            geometryTime = 0
            skyboxTime= 0
            ambientTime = 0
            pointLightTime = 0
            spotLightTime = 0
            finalPassTime = 0
        }
    }


    fun resetTimer() {
        timer = System.nanoTime()
    }


    fun logSkyBox() {
        skyboxTime += (System.nanoTime() - timer)
        resetTimer()
    }

    fun logGeometryPass() {
        geometryTime += (System.nanoTime() - timer)
        resetTimer()
    }

    fun logAmbientPass() {
        ambientTime += (System.nanoTime()-timer)
        resetTimer()
    }

    fun logSpotLightPass() {
        spotLightTime += (System.nanoTime()-timer)
        resetTimer()
    }

    fun logPointLightPass() {
        pointLightTime += (System.nanoTime()-timer)
        resetTimer()
    }

    fun logFinalPass() {
        finalPassTime += (System.nanoTime()-timer);
        resetTimer()
    }
}