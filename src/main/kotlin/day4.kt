private fun main() {
  part1 {
    val numberOfFullyRedundantRanges = sectionAssignmentPairs().count { (left, right) ->
      left in right || right in left
    }
    println("The number of fully redundant ranges is: $numberOfFullyRedundantRanges")
  }

  part2 {
    val numberOfOverlappingRanges = sectionAssignmentPairs().count { (left, right) ->
      left overlapsWith right
    }
    println("The number of overlapping ranges is: $numberOfOverlappingRanges")
  }
}

private operator fun IntRange.contains(that: IntRange): Boolean =
  first <= that.first && last >= that.last

private infix fun IntRange.overlapsWith(that: IntRange): Boolean =
  first in that || last in that || that in this

private fun sectionAssignmentPairs() = sequence {
  withInputLines("day4.txt") {
    forEach { line ->
      val (left, right) = line.split(',')
      yield(left.asAssignment() to right.asAssignment())
    }
  }
}

private fun String.asAssignment(): IntRange {
  val (lo, hi) = split('-')
  return lo.toInt()..hi.toInt()
}
