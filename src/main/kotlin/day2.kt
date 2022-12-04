import Move.*
import Outcome.*
import java.lang.IllegalStateException

private fun main() {
  part1 {
    val totalScore = playRockPaperScissors { theirs, ours ->
      val ourMove = ours.asMove()
      val theirMove = theirs.asMove()
      val outcome = outcome(theirMove, ourMove)
      ourMove.score + outcome.score
    }.sum()
    println("The total score of (not) following the strategy guide is: $totalScore")
  }

  part2 {
    val totalScore = playRockPaperScissors { theirs, ours ->
      val theirMove = theirs.asMove()
      val outcome = ours.asOutcome()
      val ourMove = outcome.counterMove(theirMove)
      ourMove.score + outcome.score
    }.sum()
    println("The total score of following the strategy guide is: $totalScore")
  }
}

private enum class Move(val id: Int) {
  Rock(0), Paper(1), Scissors(2);
  val score = id + 1
}

private enum class Outcome(val diff: Int) {
  Lose(-1), Tie(0), Win(1);
  val score = 3 * (diff + 1)
}

private fun outcome(theirs: Move, ours: Move): Outcome = when (ours.id - theirs.id) {
  -1, 2 -> Lose
  1, -2 -> Win
  else -> Tie
}

private fun Outcome.counterMove(theirs: Move): Move = when ((theirs.id + diff).mod(3)) {
  0 -> Rock
  1 -> Paper
  2 -> Scissors
  else -> throw AssertionError()
}

private inline fun <T> playRockPaperScissors(crossinline action: (theirs: Char, ours: Char) -> T) = sequence {
  withInputLines("day2.txt") {
    forEach { line ->
      yield(action(line[0], line[2]))
    }
  }
}

private fun Char.asMove() = when (this) {
  'A', 'X' -> Rock
  'B', 'Y' -> Paper
  'C', 'Z' -> Scissors
  else -> throw IllegalStateException("Cannot read move: '$this'")
}

private fun Char.asOutcome() = when (this) {
  'X' -> Lose
  'Y' -> Tie
  'Z' -> Win
  else -> throw IllegalStateException("Cannot read outcome: '$this'")
}