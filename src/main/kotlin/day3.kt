private fun main() {
  part1()
  println()
  part2()
}

private fun part1() {
  println("--- Part 1 ---")

  val totalMistakes = withRucksacks { rucksacks ->
    rucksacks.sumOf { rucksack ->
      with(rucksack) {
        assert(length % 2 == 0) { "Rucksack does not have even length: $this" }
        val split = length / 2
        val left = substring(0, split)
        val right = substring(split, length)
        (left.toSet() intersect right.toSet()).single().priority()
      }
    }
  }

  println("The total priorities of all misplaced items is: $totalMistakes")
}

private fun part2() {
  println("--- Part 2 ---")

  val totalBadges = withRucksacks { rucksacks ->
    rucksacks.chunked(3).sumOf { (a, b, c) ->
      (a.toSet() intersect b.toSet() intersect c.toSet()).single().priority()
    }
  }

  println("The sum of all badge priorities is: $totalBadges")
}

private fun Char.priority() = when (this) {
  in 'a'..'z' -> this - 'a' + 1
  in 'A'..'Z' -> this - 'A' + 27
  else -> throw AssertionError("Cannot get priority of '$this'")
}

private inline fun <T> withRucksacks(action: (rucksacks: Sequence<String>) -> T) =
  readInput("day3.txt").useLines(action)

