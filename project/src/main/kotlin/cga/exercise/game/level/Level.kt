package cga.exercise.game.level

import cga.exercise.game.chart.IoChart
import cga.exercise.game.note.Note
import cga.exercise.game.note.NoteData
import cga.exercise.game.note.NoteKey
import cga.exercise.game.staticShader
import chart.difficulty.Difficulty
import chart.difficulty._notes
import chart.info.Info
import java.io.File
import kotlin.math.abs

class Level(name: String) {
    val path = File("assets/levels/$name")

    lateinit var info: Info
    lateinit var difficulty: Difficulty
    lateinit var notes: Iterator<NoteData>
    // this could probably be done much more efficiently
    var nextNote: Note? = null

    lateinit var song: Song
    val scoring = Scoring()

    private var beat: Float = 0f
    private var beatsPerSeconds: Float = 0f


    fun setup(){
        loadChart()
        loadSounds()
    }


    fun update(dt: Float, t: Float) {
        beat = t * beatsPerSeconds
        val noteBeat = nextNote?.data?.beat ?: - 1000f // todo make decent
        if(nextNote != null && noteBeat < beat) {
            println(beat)
            println("missed Note ${nextNote?.data?.key}, ${nextNote?.data?.beat}")
            scoring.fail()
            advanceNote()
        } else if (noteBeat > beat && noteBeat < (beat + 1.0)){
            nextNote?.update(beat)
        }
    }

    fun onKey(key: NoteKey) {
        if( beat - 0.2f < nextNote!!.data.beat  && nextNote!!.data.beat < beat + 0.52){
            if (key == nextNote?.data?.key){
                val score = 1 - abs(beat - nextNote!!.data.beat)
                scoring.score(score)
                advanceNote()
            }else {
                println("wrong note")
                scoring.fail()
                advanceNote()
            }
        } else {
            scoring.fail()
        }
    }

    fun cleanup(){
        song.cleanup()
    }

    private fun loadChart(){
        val cio = IoChart(path)
        info = cio.loadInfo()
        difficulty = cio.loadLowestDiff()
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
        if (notes.hasNext())
            nextNote = Note(notes.next(), staticShader)
        else
            nextNote = null
    }
}