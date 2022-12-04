private fun main() {
  part1 {
    val totalMistakes = withRucksacks {
      sumOf { rucksack ->
        assert(rucksack.length % 2 == 0) { "Rucksack does not have even length: $this" }
        val (left, right) = rucksack.splitAt(rucksack.length / 2)
        val mistakes = left.toSet() intersect right.toSet()
        mistakes.single().priority()
      }
    }
    println("The total priorities of all misplaced items is: $totalMistakes")
  }

  part2 {
    val totalBadges = withRucksacks {
      chunked(3).sumOf { (a, b, c) ->
        val badges = a.toSet() intersect b.toSet() intersect c.toSet()
        badges.single().priority()
      }
    }
    println("The sum of all badge priorities is: $totalBadges")
  }
}

private fun String.splitAt(index: Int) = substring(0, index) to substring(index, length)

private fun Char.priority() = when (this) {
  in 'a'..'z' -> this - 'a' + 1
  in 'A'..'Z' -> this - 'A' + 27
  else -> throw AssertionError("Cannot get priority of '$this'")
}

private inline fun <T> withRucksacks(action: Sequence<String>.() -> T) =
  withInputLines("day3.txt", action)

