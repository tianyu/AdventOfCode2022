private fun main() {
  part1("The total priorities of all misplaced items is:") {
    withRucksacks {
      sumOf { rucksack ->
        assert(rucksack.length % 2 == 0) { "Rucksack does not have even length: $this" }
        val (left, right) = rucksack.splitAt(rucksack.length / 2)
        val mistakes = left.toSet() intersect right.toSet()
        mistakes.single().priority()
      }
    }
  }

  part2("The sum of all badge priorities is:") {
    withRucksacks {
      chunked(3).sumOf { (a, b, c) ->
        val badges = a.toSet() intersect b.toSet() intersect c.toSet()
        badges.single().priority()
      }
    }
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

