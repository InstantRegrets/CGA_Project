package cga.exercise.game.level

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.chart.IoChart
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.exercise.game.gameObjects.laser.LightShow
import cga.exercise.game.gameObjects.note.HitKey
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
    var nextNote:NoteData? = null
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
        val db = beatsPerSeconds * dt
        for (n in visibleNotes){
            if(n.data.beat < beat - 0.2f) {
                scoring.fail()
            } else {
                n.update(db,beat)
            }
        }
        visibleNotes.removeIf { it.data.beat < beat -0.2f }

        while (nextNote != null && nextNote!!.beat < beat+2f){
            visibleNotes.add(Note(nextNote!!, beat))
            nextNote = if (notes.hasNext()) notes.next() else null
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

    fun checkKeyPress(n: Note, key: NoteKey){
        if( beat - 0.4f < n.data.beat  && n.data.beat < beat + 0.52){
            if (key == n.data.key){
                val score = 1 - abs(beat - n.data.beat)
                scoring.score(score)
                visibleNotes.remove(n)
            }else {
                scoring.fail()
                visibleNotes.remove(n)
            }
        }
    }

    fun onKey(key: HitKey) {
        val timedNote = visibleNotes.filter { beat - 0.3f < it.data.beat && it.data.beat < beat + 0.3f }
        when(key){
            HitKey.Left -> {
                val n = timedNote.firstOrNull { it.data.key == NoteKey.Left }
                if (n != null){
                    visibleNotes.remove(n)
                    scoring.score(1 - abs(beat - n.data.beat))
                } else { scoring.fail() }
            }
            HitKey.Right -> {
                val n = timedNote.firstOrNull { it.data.key == NoteKey.Right }
                if (n != null){
                    visibleNotes.remove(n)
                    scoring.score(1 - abs(beat - n.data.beat))
                } else { scoring.fail() }
            }
            HitKey.Middle -> {
                val r = timedNote.firstOrNull { it.data.key == NoteKey.Right }
                val l = timedNote.firstOrNull { it.data.key == NoteKey.Left }
                if (r != null ){
                    visibleNotes.remove(r)
                    scoring.score(1 - abs(beat - r.data.beat))
                } else { scoring.fail() }
                if (l != null ){
                    visibleNotes.remove(l)
                    scoring.score(1 - abs(beat - l.data.beat))
                } else { scoring.fail() }
            }
        }
    }

    fun cleanup(){
        scoring.printScore()
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

        if (notes.hasNext()) nextNote = notes.next()
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
}