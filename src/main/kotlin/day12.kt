import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo

private fun main() {
  tests {
    val example = HeightMap(
      """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
  
      """.trimIndent().encodeToByteArray()
    )

    "Reading a HeightMap" {
      assertThat(example.start).isEqualTo(0)
      assertThat(example.end).isEqualTo(23)
    }

    "Getting options from a position" {
      assertThat(example.options(0).toList())
        .containsExactly(1, 9)
      assertThat(example.options(8).toList())
        .containsExactly(8 - 1, 9 + 8)
      assertThat(example.options(4 * 9).toList())
        .containsExactly(4 * 9 + 1, 3 * 9)
      assertThat(example.options(4 * 9 + 8).toList())
        .containsExactly(4 * 9 + 7, 3 * 9 + 8)
      assertThat(example.options(2 * 9 + 3).toList())
        .containsExactly(2 * 9 + 4, 2 * 9 + 2, 3 * 9 + 3, 9 + 3)
    }

    "Part 1 Example" {
      val distanceMap = example.mapTrails()
      assertThat(distanceMap[example.start]).isEqualTo(31)
    }
  }

  val heightMap = HeightMap()
  val trailsMap = heightMap.mapTrails()

  part1("The shortest path to the target location is:") {
    trailsMap[heightMap.start]
  }

  part2("The fewest steps required to move from a square with elevation 'a' to the target location is:") {
    heightMap.locationsWithElevation('a'.code.toByte())
      .minOf { trailsMap[it] }
  }
}

private class HeightMap(val bytes: ByteArray = inputStream("day12.txt").readBytes()) {
  val width = bytes.indexOf('\n'.code.toByte())
  private val lineWidth = width + 1
  val height = bytes.size / lineWidth
  val start = bytes.indexOf('S'.code.toByte())
  val end = bytes.indexOf('E'.code.toByte())

  operator fun get(index: Int): Byte = when (val byte = bytes[index]) {
    'S'.code.toByte() -> 'a'.code.toByte()
    'E'.code.toByte() -> 'z'.code.toByte()
    else -> byte
  }

  fun options(index: Int) = sequence {
    val row = index / lineWidth
    val col = index % lineWidth
    if (col < width - 1) yield(index + 1)
    if (col > 0) yield(index - 1)
    if (row < height - 1) yield(index + lineWidth)
    if (row > 0) yield(index - lineWidth)
  }

  fun mapTrails(): IntArray {
    val distanceMap = IntArray(bytes.size) { Int.MAX_VALUE }
    val positions = ArrayDeque<Int>()
    distanceMap[end] = 0
    positions.add(end)
    while (positions.isNotEmpty()) {
      val position = positions.removeFirst()
      val distance = distanceMap[position]
      val height = this[position]
      positions.addAll(
        options(position)
        .filter { distanceMap[it] == Int.MAX_VALUE && height - this[it] <= 1 }
        .onEach { distanceMap[it] = distance + 1 }
      )
    }
    return distanceMap
  }

  fun locationsWithElevation(elevation: Byte) = bytes.indices.asSequence()
    .filter { this[it] == elevation }
}
