import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue

private const val air: Byte = 0
private const val rock: Byte = 1
private const val sand: Byte = 2
private const val void: Byte = -1

fun main() {
  tests {
    "Reading rock structures" {
      val example = rockStructures("day14-example.txt")
      assertThat(example[0, 0]).isEqualTo(air)
      assertThat(example[494, 0]).isEqualTo(air)
      assertThat(example[497, 4]).isEqualTo(air)
      assertThat(example[498, 4]).isEqualTo(rock)
      assertThat(example[499, 4]).isEqualTo(air)
      assertThat(example[498, 5]).isEqualTo(rock)
      assertThat(example[498, 6]).isEqualTo(rock)
      assertThat(example[498, 7]).isEqualTo(air)
      assertThat(example[497, 6]).isEqualTo(rock)
      assertThat(example[496, 6]).isEqualTo(rock)
      assertThat(example[495, 6]).isEqualTo(air)
    }

    "Dropping sand" {
      val example = rockStructures("day14-example.txt")
      assertThat(example[500, 8]).isEqualTo(air)
      assertThat(example.dropSand(500, 0)).isTrue()
      assertThat(example[500, 8]).isEqualTo(sand)
      assertThat(example[499, 8]).isEqualTo(air)
      assertThat(example.dropSand(500, 0)).isTrue()
      assertThat(example[499, 8]).isEqualTo(sand)
      assertThat(example[498, 8]).isEqualTo(air)
      assertThat(example[500, 7]).isEqualTo(air)
      assertThat(example[501, 8]).isEqualTo(air)
      repeat(3) {
        assertThat(example.dropSand(500, 0)).isTrue()
      }
      assertThat(example[498, 8]).isEqualTo(sand)
      assertThat(example[500, 7]).isEqualTo(sand)
      assertThat(example[501, 8]).isEqualTo(sand)
      assertThat(example[497, 5]).isEqualTo(air)
      assertThat(example[495, 8]).isEqualTo(air)
      repeat(19) {
        assertThat(example.dropSand(500, 0)).isTrue()
      }
      assertThat(example[497, 5]).isEqualTo(sand)
      assertThat(example[495, 8]).isEqualTo(sand)
      assertThat(example[494, 8]).isEqualTo(air)
      assertThat(example.dropSand(500, 0)).isFalse()
    }
  }

  part1("The amount of sand that accumulates before hitting the floor is:") {
    val rockStructures = rockStructures()
    var count = 0
    while (rockStructures.dropSand(500, 0)) count += 1
    count
  }

  part2("The amount of sand that accumulates:") {
    val rockStructures = rockStructures()
    rockStructures.emulateFloor = true
    var count = 0
    while (rockStructures.dropSand(500, 0)) count += 1
    count
  }
}

data class RockStructures(private val xRange: IntRange, private val yRange: IntRange) {
  var emulateFloor = false
  private val data = ByteArray(xRange.size * yRange.size)

  private fun index(x: Int, y: Int) =
    (y - yRange.first) * xRange.size + (x - xRange.first)

  operator fun get(x: Int, y: Int): Byte {
    if (x in xRange && y in yRange) return data[index(x, y)]
    if (!emulateFloor) return void
    return if (y > yRange.last) rock else air
  }

  operator fun set(x: Int, y: Int, value: Byte) {
    if (x in xRange && y in yRange) data[index(x, y)] = value
  }

  tailrec fun dropSand(x: Int, y: Int): Boolean {
    if (this[x, y] != air) return false
    return when (air) {
      maxOf(air, this[x, y + 1]) -> dropSand(x, y + 1)
      maxOf(air, this[x - 1, y + 1]) -> dropSand(x - 1, y + 1)
      maxOf(air, this[x + 1, y + 1]) -> dropSand(x + 1, y + 1)
      else -> true.also { this[x, y] = sand }
    }
  }

  override fun toString(): String = buildString {
    for (y in yRange) {
      for (x in xRange) {
        append(
          when (get(x, y)) {
            sand -> 'o'
            rock -> '#'
            else -> ' '
          }
        )
      }
      appendLine()
    }
  }
}

fun rockStructures(resource: String = "day14.txt"): RockStructures {
  val rockStructures = withInputLines(resource) {
    var minX = 0
    var maxX = 1000
    var minY = 0
    var maxY = Int.MIN_VALUE
    flatMap { line -> line.splitToSequence(" -> ") }.forEach {
      val (x, y) = it.split(',').map(String::toInt)
      minX = minOf(minX, x)
      maxX = maxOf(maxX, x)
      minY = minOf(minY, y)
      maxY = maxOf(maxY, y)
    }
    RockStructures(minX..maxX, minY..maxY + 1)
  }

  withInputLines(resource) {
    forEach { line ->
      line.split(" -> ").map {
        val (x, y) = it.split(',').map(String::toInt)
        x to y
      }.zipWithNext { (startX, startY), (endX, endY) ->
        when {
          startX == endX -> {
            for (y in if (startY <= endY) startY..endY else endY..startY) {
              rockStructures[startX, y] = 1
            }
          }
          startY == endY -> {
            for (x in if (startX <= endX) startX..endX else endX..startX) {
              rockStructures[x, startY] = 1
            }
          }
          else -> throw AssertionError("Can't draw diagonals")
        }
      }
    }
  }

  return rockStructures
}