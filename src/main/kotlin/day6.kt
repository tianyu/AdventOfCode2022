import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import java.io.Reader

private fun main() {
  tests {
    "Part 1 examples" {
      assertThat("bvwbjplbgvbhsrlpgdmjqwftvncz".reader().firstDistinctSubsequence(4)).isEqualTo(5)
      assertThat("nppdvjthqldpwncqszvftbrmjlhg".reader().firstDistinctSubsequence(4)).isEqualTo(6)
      assertThat("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg".reader().firstDistinctSubsequence(4)).isEqualTo(10)
      assertThat("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw".reader().firstDistinctSubsequence(4)).isEqualTo(11)
    }

    "Part 2 examples" {
      assertThat("mjqjpqmgbljsphdztnvjfqwrcgsmlb".reader().firstDistinctSubsequence(14)).isEqualTo(19)
      assertThat("bvwbjplbgvbhsrlpgdmjqwftvncz".reader().firstDistinctSubsequence(14)).isEqualTo(23)
      assertThat("nppdvjthqldpwncqszvftbrmjlhg".reader().firstDistinctSubsequence(14)).isEqualTo(23)
      assertThat("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg".reader().firstDistinctSubsequence(14)).isEqualTo(29)
      assertThat("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw".reader().firstDistinctSubsequence(14)).isEqualTo(26)
    }

    "The ability to check if characters are distinct" {
      with("".toCharArray()) {
        assertThat(isDistinct()).isTrue()
        assertThat(concatToString()).isEqualTo("")
      }

      with("x".toCharArray()) {
        assertThat(isDistinct()).isTrue()
        assertThat(concatToString()).isEqualTo("x")
      }

      with("xy".toCharArray()) {
        assertThat(isDistinct()).isTrue()
        assertThat(concatToString()).isEqualTo("xy")
      }

      with("yx".toCharArray()) {
        assertThat(isDistinct()).isTrue()
        assertThat(concatToString()).isEqualTo("xy")
      }

      with("xx".toCharArray()) {
        assertThat(isDistinct()).isFalse()
        assertThat(concatToString()).isEqualTo("xx")
      }

      with("xyz".toCharArray()) {
        assertThat(isDistinct()).isTrue()
        assertThat(concatToString()).isEqualTo("xyz")
      }

      with("zyx".toCharArray()) {
        assertThat(isDistinct()).isTrue()
        assertThat(concatToString()).isEqualTo("xyz")
      }

      with("yzx".toCharArray()) {
        assertThat(isDistinct()).isTrue()
        assertThat(concatToString()).isEqualTo("xyz")
      }

      with("xxy".toCharArray()) {
        assertThat(isDistinct()).isFalse()
        assertThat(concatToString()).isEqualTo("xxy")
      }

      with("xyx".toCharArray()) {
        assertThat(isDistinct()).isFalse()
        assertThat(concatToString()).isEqualTo("xxy")
      }

      with("yxx".toCharArray()) {
        assertThat(isDistinct()).isFalse()
        assertThat(concatToString()).isEqualTo("xxy")
      }
    }
  }

  part1("The first start of packet is at position:") {
    datastream().firstDistinctSubsequence(4)
  }

  part2("The first start-of-message is at position:") {
    datastream().firstDistinctSubsequence(14)
  }
}

private fun datastream() = readInput("day6.txt")

private fun Reader.firstDistinctSubsequence(distinctChars: Int): Int {
  val buffer = CharArray(distinctChars)
  val workspace = CharArray(distinctChars)
  var readIndex = read(buffer) - 1
  while (!buffer.copyInto(workspace).isDistinct()) {
    buffer[++readIndex % distinctChars] = read().toChar()
  }
  return readIndex + 1
}

private fun CharArray.isDistinct(begin: Int = 0, end: Int = size): Boolean {
  if (end == begin || end == begin + 1) return true

  val pivot = get(begin)
  var lo = begin + 1
  var hi = end
  while (lo < hi) {
    val char = this[lo]
    if (char == pivot) {
      return false
    } else if (char < pivot) {
      this[lo - 1] = char
      this[lo] = pivot
      lo += 1
    } else {
      this[lo] = this[hi - 1]
      this[hi - 1] = char
      hi -= 1
    }
  }
  return isDistinct(begin, lo - 1) && isDistinct(lo, end)
}
