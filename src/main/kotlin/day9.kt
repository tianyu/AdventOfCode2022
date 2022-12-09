import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.math.absoluteValue
import kotlin.math.sign

private fun main() {
  tests {
    val example = """
      R 4
      U 4
      L 3
      D 1
      R 4
      D 1
      L 5
      R 2
    """.trimIndent()

    "Reading head movements" {
      val headMovements = example.lineSequence().headMovements().toList()
      assertThat(headMovements.size).isEqualTo(24)
      assertThat(headMovements.fold(Vec(0, 0), Vec::plus))
        .isEqualTo(Vec(2, 2))
    }

    "Part 1 Example" {
      assertThat(example.lineSequence().headMovements().tailPositions(rope(2)).toSet().size)
        .isEqualTo(13)
    }

    "Part 2 Examples" {
      assertThat(example.lineSequence().headMovements().tailPositions(rope(10)).toSet().size)
        .isEqualTo(1)

      assertThat("""
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
      """.trimIndent().lineSequence()
        .headMovements()
        .tailPositions(rope(10))
        .toSet().size
      ).isEqualTo(36)
    }
  }

  part1("The number of distinct tail positions for a 2-knot rope is:") {
    headMovements().tailPositions(rope(2)).toSet().size
  }

  part2("The number of distinct tail positions for a 10-knot rope is:") {
    headMovements().tailPositions(rope(10)).toSet().size
  }
}

private fun rope(size: Int, origin: Vec = Vec(0, 0)) = Array(size) { origin }

private fun Sequence<Vec>.tailPositions(rope: Array<Vec>) = sequence {
  yield(rope.last())
  for (headMovement in this@tailPositions) {
    rope[0] += headMovement
    var previous = rope[0]
    for (index in 1 until rope.size) {
      val stretch = previous - rope[index]
      if (stretch.magnitude <= 1) break
      rope[index] += stretch.sign
      previous = rope[index]
      if (index == rope.lastIndex) yield(previous)
    }
  }
}

private data class Vec(val x: Int, val y: Int) {
  operator fun plus(that: Vec): Vec = Vec(x + that.x, y + that.y)
  operator fun minus(that: Vec): Vec = Vec(x - that.x, y - that.y)
  val magnitude: Int = maxOf(x.absoluteValue, y.absoluteValue)
  val sign: Vec get() = Vec(x.sign, y.sign)
}

private fun headMovements() = sequence {
  withInputLines("day9.txt") {
    yieldAll(headMovements())
  }
}

private fun Sequence<String>.headMovements(): Sequence<Vec> {
  val left = Vec(-1, 0)
  val right = Vec(1, 0)
  val up = Vec(0, 1)
  val down = Vec(0, -1)
  return flatMap {
    sequence {
      val direction = when (it[0]) {
        'L' -> left
        'R' -> right
        'U' -> up
        'D' -> down
        else -> throw AssertionError("Unknown direction: ${it[0]}")
      }
      repeat(it.substring(2).toInt()) { yield(direction) }
    }
  }
}