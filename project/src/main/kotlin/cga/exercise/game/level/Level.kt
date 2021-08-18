package cga.exercise.game.level

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.chart.IoChart
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.exercise.game.gameObjects.laser.LightShow
import cga.exercise.game.gameObjects.note.Note
import cga.exercise.game.gameObjects.note.NoteData
import cga.exercise.game.gameObjects.note.NoteKey
import cga.framework.GameWindow
import chart.difficulty.Difficulty
import chart.difficulty._events
import chart.difficulty._notes
import chart.info.Info
import org.joml.Matrix4f
import java.io.File
import kotlin.math.abs

class Level: GameObject {
    lateinit var path: File

    lateinit var info: Info
    lateinit var difficulty: Difficulty
    lateinit var notes: Iterator<NoteData>
    lateinit var events: Iterator<Event>
    // this could probably be done much more efficiently
    var visibleNotes: ArrayList<Note> = arrayListOf()
    var nextEvent: Event = Event(0f,Event.Type.BackLasers,Event.Effect.LightOff)

    val lightShow = LightShow()

    lateinit var song: Song
    val scoring = Scoring()

    var beat: Float = 0f
    var beatsPerSeconds: Float = 0f


    fun setup(){
        loadChart()
        loadSounds()
    }

    override fun draw(shaderProgram: ShaderProgram) {
        lightShow.draw(shaderProgram)
        for (n in visibleNotes){
            n.draw(shaderProgram)
        }
    }


    override fun update(dt: Float, beat: Float) {
        this.beat = beat
        for (n in visibleNotes){
            if(n.data.beat < beat - 0.2f) {
                println(beat)
                println("missed ${n.data.key} Note on beat ${n.data.beat} at $beat")
                scoring.fail()
                advanceNote()
            } else if (n.data.beat > beat && n.data.beat < (beat + 2.0)){
                n.update(dt,beat)
            }
        }
        while (nextEvent.beat < beat && events.hasNext()){
            lightShow.fire(nextEvent)
            nextEvent = events.next()
        }
        lightShow.update(dt,beat)
    }

    override fun processInput(window: GameWindow, dt: Float) {
        lightShow.processInput(window, dt)
    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
        visibleNotes.forEach { it.processLighting(shaderProgram, viewMatrix4f) }
        lightShow.processLighting(shaderProgram, viewMatrix4f)
    }

    override fun switchPhase(phase: Phase) {
        lightShow.switchPhase(phase)
        visibleNotes.forEach { it.switchPhase(phase) }
    }

    fun onKey(key: NoteKey) {
        for (n in visibleNotes){
            if( beat - 0.4f < n.data.beat  && n.data.beat < beat + 0.52){
                if (key == n.data.key){
                    val score = 1 - abs(beat - n.data.beat)
                    scoring.score(score)
                    advanceNote()
                }else {
                    println("wrong note")
                    scoring.fail()
                    advanceNote()
                }
            }
         else {
                 scoring.fail()
             }
        }
    }

    fun cleanup(){
        song.cleanup()
    }

    private fun loadChart(){
        val cio = IoChart()
        info = cio.info
        difficulty = cio.loadLowestDiff()
        path = cio.chartPath
        notes = difficulty._notes
                .sortedBy { it._time }
                .map { noteDataFromBsNote(it) }
                .toSet().iterator()
        events = difficulty._events
            .sortedBy { it._time }
            .map { eventFromBsEvent(it) }
            .iterator()

        advanceNote()
        beatsPerSeconds = (info._beatsPerMinute / 60f).toFloat()
    }

    private fun noteDataFromBsNote(note: _notes): NoteData {
        val key = when(note._type){
            0 -> NoteKey.Left
            else -> NoteKey.Right
        }
        return NoteData(note._time.toFloat(), key)
    }

    private fun eventFromBsEvent(event: _events): Event {
        val type = when(event._type){
            0 -> Event.Type.BackLasers
            1 -> Event.Type.RingLights
            2 -> Event.Type.LeftRotatingLasers
            3 -> Event.Type.RightRotationgLasers
            5 -> Event.Type.CenterLights
            else -> Event.Type.CenterLights // unused
        }
        val effect = when(event._value){
            0 -> Event.Effect.LightOff
            1 -> Event.Effect.LightOnBlue
            2 -> Event.Effect.LightFlashBlue
            3 -> Event.Effect.LightFadeBlue
            5 -> Event.Effect.LightOnRed
            6 -> Event.Effect.LightFlashRed
            7 -> Event.Effect.LightFadeRed
            else -> Event.Effect.LightFadeRed // unused
        }
        return Event(event._time.toFloat(), type, effect)
    }

    private fun loadSounds(){
        val songfile = path.resolve(info._songFilename)
        song = Song(songfile)
    }

    private fun advanceNote(){
        if (visibleNotes.isNotEmpty())
            visibleNotes.removeAt(0)
        if (notes.hasNext())
            visibleNotes.add(Note(notes.next()))
    }
}