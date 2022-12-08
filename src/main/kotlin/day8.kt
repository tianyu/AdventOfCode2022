import assertk.Assert
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import java.io.InputStream

private fun main() {
  val example = """
    30373
    25512
    65332
    33549
    35390
    
  """.trimIndent().byteInputStream().readTreeMap()
  tests {
    "Read height map" {
      assertThat(example.width to example.height).isEqualTo(5 to 5)
    }

    "Get a row of the heightMap" {
      assertThat(example.row(0)).containsBytes("30373")
      assertThat(example.row(2)).containsBytes("65332")
      assertThat(example.row(4)).containsBytes("35390")
    }

    "Get a reversed row of the heightMap" {
      assertThat(example.rrow(0)).containsBytes("37303")
      assertThat(example.rrow(2)).containsBytes("23356")
      assertThat(example.rrow(4)).containsBytes("09353")
    }

    "Get a column of the heightMap" {
      assertThat(example.col(0)).containsBytes("32633")
      assertThat(example.col(2)).containsBytes("35353")
      assertThat(example.col(4)).containsBytes("32290")
    }

    "Get a reversed column of the heightMap" {
      assertThat(example.rcol(0)).containsBytes("33623")
      assertThat(example.rcol(2)).containsBytes("35353")
      assertThat(example.rcol(4)).containsBytes("09223")
    }

    "Get the view from a tree to its right" {
      assertThat(example.viewRight(1, 2)).containsBytes("12")
      assertThat(example.viewRight(2, 4)).containsBytes("")
      assertThat(example.viewRight(3, 2)).containsBytes("49")
      assertThat(example.viewRight(4, 4)).containsBytes("")
    }

    "Get the view from a tree to its left" {
      assertThat(example.viewLeft(1, 2)).containsBytes("52")
      assertThat(example.viewLeft(2, 0)).containsBytes("")
      assertThat(example.viewLeft(3, 2)).containsBytes("33")
      assertThat(example.viewLeft(0, 0)).containsBytes("")
    }

    "Get the view from a tree down" {
      assertThat(example.viewDown(1, 2)).containsBytes("353")
      assertThat(example.viewDown(4, 4)).containsBytes("")
      assertThat(example.viewDown(3, 2)).containsBytes("3")
      assertThat(example.viewDown(4, 1)).containsBytes("")
    }

    "Get the view from a tree up" {
      assertThat(example.viewUp(1, 2)).containsBytes("3")
      assertThat(example.viewUp(0, 0)).containsBytes("")
      assertThat(example.viewUp(3, 2)).containsBytes("353")
      assertThat(example.viewUp(0, 3)).containsBytes("")
    }

    "Get the scenic score of a tree" {
      assertThat(example.scenicScore(1, 2)).isEqualTo(4L)
      assertThat(example.scenicScore(3, 2)).isEqualTo(8L)
    }
  }

  val treeMap = treeMap()
  part1("The number of visible trees is:") {
    val visibility = TreeMap(ByteArray(treeMap.data.size), treeMap.width)

    for (row in 0 until treeMap.height) {
      var maxFromLeft: Byte = -1
      var maxFromRight: Byte = -1
      treeMap.row(row).forEachIndexed { col, height ->
        if (height > maxFromLeft) {
          maxFromLeft = height
          visibility[row, col] = 1
        }
      }
      treeMap.rrow(row).forEachIndexed { rcol, height ->
        if (height > maxFromRight) {
          maxFromRight = height
          visibility[row, treeMap.width - 1 - rcol] = 1
        }
      }
    }

    for (col in 0 until treeMap.width) {
      var maxFromTop: Byte = -1
      var maxFromBottom: Byte = -1
      treeMap.col(col).forEachIndexed { row, height ->
        if (height > maxFromTop) {
          maxFromTop = height
          visibility[row, col] = 1
        }
      }
      treeMap.rcol(col).forEachIndexed { rrow, height ->
        if (height > maxFromBottom) {
          maxFromBottom = height
          visibility[treeMap.height - 1 - rrow, col] = 1
        }
      }
    }

    visibility.data.sum()
  }

  part2("The best scenic score is:") {
    sequence {
      for (row in 0 until treeMap.height) {
        for (col in 0 until treeMap.width) {
          yield(treeMap.scenicScore(row, col))
        }
      }
    }.max()
  }
}

private class TreeMap(val data: ByteArray, val width: Int) {
  val height = data.size / (width + 1)

  operator fun get(row: Int, col: Int) = data[row * (width + 1) + col]

  operator fun set(row: Int, col: Int, value: Byte) {
    data[row * (width + 1) + col] = value
  }

  fun row(row: Int): Sequence<Byte> = sequence {
    val start = row * (width + 1)
    val end = start + width
    for (i in start until end) yield(data[i])
  }

  fun rrow(row: Int): Sequence<Byte> = sequence {
    val start = row * (width + 1)
    val end = start + width - 1
    for (i in end downTo start) yield(data[i])
  }

  fun col(col: Int): Sequence<Byte> = sequence {
    val end = height * (width + 1) + col
    for (i in col until end step width + 1) yield(data[i])
  }

  fun rcol(col: Int): Sequence<Byte> = sequence {
    val end = (height - 1) * (width + 1) + col
    for (i in end downTo col step width + 1) yield(data[i])
  }

  fun viewRight(row: Int, col: Int): Sequence<Byte> = sequence {
    val start = row * (width + 1) + col + 1
    val end = row * (width + 1) + width
    for (i in start until end) yield(data[i])
  }

  fun viewLeft(row: Int, col: Int): Sequence<Byte> = sequence {
    val start = row * (width + 1) + col - 1
    val end = row * (width + 1)
    for (i in start downTo  end) yield(data[i])
  }

  fun viewDown(row: Int, col: Int): Sequence<Byte> = sequence {
    val start = (row + 1) * (width + 1) + col
    val end = height * (width + 1) + col
    for (i in start until end step width + 1) yield(data[i])
  }

  fun viewUp(row: Int, col: Int): Sequence<Byte> = sequence {
    val start = (row - 1) * (width + 1) + col
    val end = col
    for (i in start downTo end step width + 1) yield(data[i])
  }

  fun scenicScore(row: Int, col: Int): Long {
    val height = get(row, col)
    return sequenceOf(
      viewRight(row, col),
      viewLeft(row, col),
      viewDown(row, col),
      viewUp(row, col),
    ).map { view ->
      var score = 0L
      for (tree in view) {
        score += 1
        if (tree >= height) break
      }
      score
    }.reduce(Long::times)
  }
}

private fun treeMap() = inputStream("day8.txt").use(InputStream::readTreeMap)

private fun InputStream.readTreeMap(): TreeMap {
  val bytes = readBytes()
  val width = bytes.indexOfFirst { it == '\n'.code.toByte() }
  return TreeMap(bytes, width)
}

private fun Assert<Sequence<Byte>>.containsBytes(bytes: String) =
  transform { it.toList() }.containsExactly(*bytes.toByteArray().toTypedArray())