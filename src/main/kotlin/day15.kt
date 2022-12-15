import assertk.assertThat
import assertk.assertions.*
import kotlin.math.absoluteValue

private fun main() {
  tests {
    val sensors = sensors("day15-example.txt")
    "Read sensors" {
      assertThat(sensors).containsAll(
        Sensor(2, 18, -2, 15),
        Sensor(10, 20, 10, 16),
        Sensor(0, 11, 2, 10),
        Sensor(20, 1, 15, 3),
      )
    }

    "Sensor range" {
      assertThat(Sensor(9, 16, 10, 16).range).isEqualTo(1)
      assertThat(Sensor(8, 7, 2, 10).range).isEqualTo(9)
    }

    "Sensor coverage" {
      val sensor = Sensor(8, 7, 2, 10)
      assertThat(sensor.coverage(10)).isEqualTo(2..14)
      assertThat(sensor.coverage(7)).isEqualTo(-1..17)
      assertThat(sensor.coverage(16)).isEqualTo(8..8)
      assertThat(sensor.coverage(-2)).isEqualTo(8..8)
      assertThat(sensor.coverage(-3)).isEmpty()
      assertThat(sensor.coverage(17)).isEmpty()
    }

    "Part 1 Example" {
      assertThat(sensors.totalCoverage(10)).isEqualTo(26)
    }

    "Inverting a range" {
      assertThat((0..100) - listOf()).containsExactly(0..100)
      assertThat((0..100) - listOf(-3..-1)).containsExactly(0..100)
      assertThat((0..100) - listOf(101..105)).containsExactly(0..100)
      assertThat((0..100) - listOf(0..50)).containsExactly(51..100)
      assertThat((0..100) - listOf(80..100)).containsExactly(0..79)
      assertThat((0..100) - listOf(0..100)).isEmpty()
      assertThat((0..100) - listOf(-5..20, 70..110)).containsExactly(21..69)
      assertThat((0..100) - listOf(25..49, 76..99)).containsExactly(0..24, 50..75, 100..100)
      assertThat((0..100) - listOf(0..15, 18..30, 96..105)).containsExactly(16..17, 31..95)
    }

    "Part 2 Example" {
      assertThat(sensors.findBeaconOptions(0..20, 0..20)).containsOnly(14 to 11)
    }
  }

  val sensors = sensors()

  part1("The number of positions that cannot contain a beacon at y=2000000 is:") {
    sensors.totalCoverage(2_000_000)
  }

  part2("The tuning frequency of the distress beacon is:") {
    val size = 4_000_000
    val (x, y) = sensors.findBeaconOptions(0..size, 0..size).first()
    x.toLong() * size + y.toLong()
  }
}

private data class Sensor(val x: Int, val y: Int, val beaconX: Int, val beaconY: Int) {
  val range = (x - beaconX).absoluteValue + (y - beaconY).absoluteValue

  fun coverage(lineY: Int): IntRange {
    // find lineX where:
    // |x - lineX| + |y - lineY| <= range
    // |x - lineX| <= range - |y - lineY|
    val k = range - (y - lineY).absoluteValue
    // lineX - x <= k and x - lineX <= k
    // lineX <= x + k and lineX >= x - k
    // x - k <= lineX <= x + k
    return x - k .. x + k
  }
}

private fun List<Sensor>.totalCoverage(lineY: Int): Int {
  val beacons = mapNotNullTo(mutableSetOf()) { if (it.beaconY == lineY) it.beaconX else null }
  return asSequence()
    .map { it.coverage(lineY) }
    .coalesce()
    .sumOf { it.size - beacons.count(it::contains) }
}

private fun List<Sensor>.findBeaconOptions(xrange: IntRange, yrange: IntRange): Set<Pair<Int, Int>> {
  val beacons = mapTo(mutableSetOf()) { it.beaconX to it.beaconY }
  val options = yrange.asSequence().flatMapTo(mutableSetOf()) { y ->
    val coverage = asSequence().map { it.coverage(y) }.sortedBy { it.first }.coalesce()
    val options = xrange - coverage
    options.flatMap { xs ->
      xs.map { x -> x to y }
    }
  }
  return options - beacons
}

private fun Sequence<IntRange>.coalesce() = buildList<IntRange> {
  val ranges = this@coalesce.iterator()
  if (!ranges.hasNext()) return@buildList
  add(ranges.next())
  for (range in ranges) {
    when {
      range.isEmpty() -> continue
      range.first in last() -> set(lastIndex, last().first..maxOf(last().last, range.last))
      else -> add(range)
    }
  }
}

private operator fun IntRange.minus(ranges: List<IntRange>) = buildList {
  addInversions(this@minus, ranges.iterator())
}

private tailrec fun MutableList<IntRange>.addInversions(outer: IntRange, inner: Iterator<IntRange>) {
  if (outer.isEmpty()) return
  if (!inner.hasNext()) add(outer).also { return }
  val next = inner.next()
  if (outer.first < next.first) {
    add(outer.first..minOf(outer.last, next.first - 1))
  }
  addInversions(next.last + 1..outer.last, inner)
}

private fun sensors(resource: String = "day15.txt") = buildList {
  withInputLines(resource) {
    this@withInputLines.forEach { line ->
      val (sensorX, sensorY, beaconX, beaconY) = Regex("-?\\d+").findAll(line)
        .map { it.value.toInt() }.toList()
      add(Sensor(sensorX, sensorY, beaconX, beaconY))
    }
  }
  sortBy { sensor: Sensor -> sensor.x - sensor.range }
}