package cga.exercise.game.level

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.chart.IoChart
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.note.Note
import cga.exercise.game.note.NoteData
import cga.exercise.game.note.NoteKey
import cga.framework.GameWindow
import chart.difficulty.Difficulty
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
    // this could probably be done much more efficiently
    var visibleNotes: ArrayList<Note> = arrayListOf()

    lateinit var song: Song
    val scoring = Scoring()

    var beat: Float = 0f
    var beatsPerSeconds: Float = 0f


    fun setup(){
        loadChart()
        loadSounds()
    }

    override fun draw(shaderProgram: ShaderProgram) {
        for (n in visibleNotes){
            n.draw(shaderProgram)
        }
    }


    override fun update(dt: Float, beat: Float) {
        this.beat = beat
        for (n in visibleNotes){
            if(n.data.beat < beat) {
                println(beat)
                println("missed Note ${n.data.key} on beat ${n.data.beat}")
                scoring.fail()
                advanceNote()
            } else if (n.data.beat > beat && n.data.beat < (beat + 1.0)){
                n.update(dt,beat)
            }
        }
    }

    override fun processInput(window: GameWindow, dt: Float) {

    }

    override fun processLighting(shaderProgram: ShaderProgram, viewMatrix4f: Matrix4f) {
        visibleNotes.forEach { it.processLighting(shaderProgram, viewMatrix4f) }
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

    private fun loadSounds(){
        val songfile = path.resolve(info._songFilename)
        song = Song(songfile)
        song.play()
    }

    private fun advanceNote(){
        if (visibleNotes.isNotEmpty())
            visibleNotes.removeAt(0)
        if (notes.hasNext())
            visibleNotes.add(Note(notes.next()))
    }
}