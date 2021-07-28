package cga.exercise.game.chart.difficulty


// only created to make choosing difficulty easier
enum class DifficultyEnum(val rank: Int) {
    Easy(1),
    Normal(3),
    Hard(5),
    Expert(7),
    ExpertPlus(9)
}