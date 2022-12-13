import Packet.Mono
import Packet.Multi
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isLessThan

private fun main() {
  tests {
    "Parse a packet" {
      assertThat(Packet("[1,1,3,1,1]")).isEqualTo(
        Multi(Mono(1), Mono(1), Mono(3), Mono(1), Mono(1))
      )
      assertThat(Packet("[[1],[2,3,4]]")).isEqualTo(
        Multi(
          Multi(Mono(1)),
          Multi(Mono(2), Mono(3), Mono(4))
        )
      )
      assertThat(Packet("[[],1000]")).isEqualTo(
        Multi(
          Multi(),
          Mono(1000)
        )
      )
    }

    "Comparing packets" {
      assertThat(Packet("[1,1,3,1,1]")).isLessThan(Packet("[1,1,5,1,1]"))
      assertThat(Packet("[[1],[2,3,4]]")).isLessThan(Packet("[[1],4]"))
      assertThat(Packet("[9]")).isGreaterThan(Packet("[[8,7,6]]"))
      assertThat(Packet("[[4,4],4,4]")).isLessThan(Packet("[[4,4],4,4,4]"))
      assertThat(Packet("[[[]]]")).isGreaterThan(Packet("[[]]"))
      assertThat(Packet("[1,[2,[3,[4,[5,6,7]]]],8,9]")).isGreaterThan(Packet("[1,[2,[3,[4,[5,6,0]]]],8,9]"))
    }
  }

  part1("The sum of the indicies of all the valid packet pairs is:") {
    packets().chunked(2).mapIndexedNotNull { index, (left, right) ->
      if (left <= right) index + 1 else null
    }.sum()
  }

  part2("The decoder key for the distress signal is:") {
    val dividers = listOf(Packet("[[2]]"), Packet("[[6]]"))
    val packets = buildList {
      addAll(dividers)
      addAll(packets())
      sort()
    }
    dividers.map { packets.indexOf(it) + 1 }.fold(1, Int::times)
  }
}

private fun Packet(text: String) = text.reader().cursor().readPacket()

private sealed interface Packet: Comparable<Packet> {
  data class Mono(val value: Int): Packet {
    override fun compareTo(other: Packet): Int = when (other) {
      is Mono -> value.compareTo(other.value)
      is Multi -> Multi(this).compareTo(other)
    }
  }

  data class Multi(val values: List<Packet>): Packet {
    constructor(vararg values: Packet): this(values.toList())

    override fun compareTo(other: Packet): Int = when (other) {
      is Mono -> compareTo(Multi(other))
      is Multi -> {
        repeat(minOf(values.size, other.values.size)) {
          val result = values[it].compareTo(other.values[it])
          if (result != 0) return result
        }
        values.size.compareTo(other.values.size)
      }
    }
  }
}

private fun packets() = sequence {
  readInput("day13.txt").use {
    do {
      yield(it.cursor().readPacket())
      yield(it.cursor().readPacket())
    } while (it.read() == '\n'.code)
  }
}

private fun Cursor.readPacket(): Packet = when (codepoint) {
  in '0'.code..'9'.code -> readMonoPacket()
  '['.code -> readMultiPacket()
  else -> throw AssertionError("Unexpected start of packet: ${codepoint.toChar()}")
}

private fun Cursor.readMonoPacket(): Mono {
  val value = buildString {
    while (codepoint in '0'.code .. '9'.code) {
      appendCodePoint(codepoint)
      advance()
    }
  }
  return Mono(value.toInt())
}

private fun Cursor.readMultiPacket(): Multi {
  val values = buildList {
    while (codepoint != ']'.code && advance() != ']'.code) {
      add(readPacket())
    }
  }
  advance()
  return Multi(values)
}
