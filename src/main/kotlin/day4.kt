private fun main() {
  part1 {
    val numberOfFullyRedundantRanges = sectionAssignmentPairs().count { (left, right) ->
      left in right || right in left
    }
    println("The number of fully redundant ranges is: $numberOfFullyRedundantRanges")
  }

  part2 {
    val numberOfOverlappingRanges = sectionAssignmentPairs().count { (left, right) ->
      left.first in right || left.last in right || right in left
    }
    println("The number of overlapping ranges is: $numberOfOverlappingRanges")
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
