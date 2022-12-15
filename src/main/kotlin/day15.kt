import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.isEqualTo
import kotlin.math.absoluteValue

fun main() {
  tests {
    "Read sensors" {
      val sensors = sensors("day15-example.txt")
      assertThat(sensors).containsAll(
        Sensor(2, 18, -2, 15),
        Sensor(10, 20, 10, 16),
        Sensor(0, 11, 2, 10),
        Sensor(20, 1, 15, 3),
      )
    }

    "Sensor range" {
      assertThat(Sensor(2, 18, -2, 15).range).isEqualTo(9)
    }
  }
}

data class Sensor(val x: Int, val y: Int, val beaconX: Int, val beaconY: Int) {
  val range = (x - beaconX).absoluteValue + (y - beaconY).absoluteValue
}

fun sensors(resource: String = "day15.txt") = mutableListOf<Sensor>().apply {
  withInputLines(resource) {
    forEach { line ->
      val (sensorX, sensorY, beaconX, beaconY) = Regex("\\d+").findAll(line)
        .map { it.value.toInt() }.toList()
      add(Sensor(sensorX, sensorY, beaconX, beaconY))
    }
  }
}