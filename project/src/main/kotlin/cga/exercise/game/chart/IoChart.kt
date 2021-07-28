package cga.exercise.game.chart

import cga.exercise.game.chart.difficulty.DifficultyEnum
import chart.difficulty.Difficulty
import chart.info.Info
import com.google.gson.Gson
import java.io.File

class IoChart(val chartPath: File){
    private val gson = Gson()

    fun loadInfo(): Info {
        if (!chartPath.isDirectory)
            throw Exception("Invalid Path, song $chartPath does not exist")

        var infoPath = chartPath.resolve("info.dat")
        if (!infoPath.isFile)
            infoPath = chartPath.resolve("Info.dat")
        if (!infoPath.isFile)
            throw Exception("Invalid info Path, info $infoPath does not exist")

        var infoJson = infoPath.readText()
        if (!infoJson.startsWith("{"))
            infoJson = infoPath.readText(charset("UTF16"))

        return gson.fromJson(infoJson, Info::class.java)
    }

    fun loadLowestDiff(): Difficulty {
        val info = loadInfo()
        val bms = info._difficultyBeatmapSets.find { it._beatmapCharacteristicName == "Standard" }
        if (bms != null && bms._difficultyBeatmaps.isNotEmpty())
            return loadDifficulty(bms._difficultyBeatmaps.first()._difficultyRank)
        else{
            val set = info._difficultyBeatmapSets.first()
            return loadDifficulty(set._difficultyBeatmaps.first()._difficultyRank, set._beatmapCharacteristicName)
        }
    }

    fun loadDifficulty(difficulty: DifficultyEnum, characteristic: String = "Standard"){
        loadDifficulty(difficulty.rank, characteristic)
    }

    fun loadDifficulty(difficultyRank: Int, characteristic: String = "Standard"): Difficulty {
        val info = loadInfo()

        var diffFile: File? = null
        var offset = 0.0

        // find the target Difficulty
        for (bms in info._difficultyBeatmapSets){
            for (bm in bms._difficultyBeatmaps){
                if (bms._beatmapCharacteristicName.equals(characteristic, ignoreCase = true) && bm._difficultyRank == difficultyRank){
                    offset = (info._songTimeOffset + (bm._customData?._editorOffset?:0))
                    diffFile = chartPath.resolve(bm._beatmapFilename)
                }
            }
        }

        if (diffFile == null) {
            var s = ("The Difficulty $characteristic $difficultyRank does not exist.")
            s += "\nAll available Difficulties:"
            for (d in info._difficultyBeatmapSets){
                s+="\n              Characteristic: ${d._beatmapCharacteristicName}:"
                for(m in d._difficultyBeatmaps){
                    s += "\n               - ${m._difficulty}"
                }
            }
            throw Exception(s)
        }

        val diffJson = diffFile.readText()

        val diff = gson.fromJson(diffJson, Difficulty::class.java)

        diff.bpm = info._beatsPerMinute
        diff.offset = offset
        diff.file = diffFile

        return diff
    }
}

