private fun main() {
  part1("The number of fully overlapping assignments is:") {
    sectionAssignmentPairs().count { (left, right) ->
      left in right || right in left
    }
  }

  part2("The number of partially overlapping assignments is:") {
    sectionAssignmentPairs().count { (left, right) ->
      left.first in right || left.last in right || right in left
    }
  }
}

private operator fun IntRange.contains(that: IntRange): Boolean =
  first <= that.first && last >= that.last

private fun sectionAssignmentPairs() = sequence {
  withInputLines("day4.txt") {
    forEach { line ->
      val (lo1, hi1, lo2, hi2) = line.split('-', ',').map(String::toInt)
      yield(lo1..hi1 to lo2..hi2)
    }
  }
}
