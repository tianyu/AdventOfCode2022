private fun main() {
  part1 {
    val crateMover9000 = CargoCrane { stacks, count, from, to ->
      stacks[to].append(stacks[from].unappend(count).reversed())
    }

    val topCrates = crateMover9000.moveCrates().topCrates()
    println("Using the CrateMover9000, the crates at the top are: $topCrates")
  }

  part2 {
    val crateMover9001 = CargoCrane { stacks, count, from, to ->
      stacks[to].append(stacks[from].unappend(count))
    }

    val topCrates = crateMover9001.moveCrates().topCrates()
    println("Using the CrateMover9001, the crates at the top are: $topCrates")
  }
}

private fun interface CargoCrane {
  fun move(stacks: List<StringBuilder>, count: Int, from: Int, to: Int)
}

private fun CargoCrane.moveCrates(): List<CharSequence> = withInputLines("day5.txt") {
  val lines = iterator()
  val stacks = lines.readStacks()

  assert(lines.next() == "")

  for (line in lines) {
    if (!line.startsWith("move ")) break
    val (count, from, to) = line.split(' ').mapNotNull(String::toIntOrNull)
    move(stacks, count, from - 1, to - 1)
  }

  stacks
}

private fun Iterator<String>.readStacks(): List<StringBuilder> = buildList {
  for (line in this@readStacks) {
    if (line.startsWith(" 1 ")) break
    val numColumns = (line.length + 1) / 4
    ensureSize(numColumns, ::StringBuilder)
    repeat(numColumns) {
      val crate = line[4 * it + 1]
      if (crate != ' ') this[it].append(crate)
    }
  }

  forEach(StringBuilder::reverse)
}

private inline fun <T> MutableList<T>.ensureSize(n: Int, init: () -> T) {
  repeat((size until n).count()) {
    add(init())
  }
}

private fun StringBuilder.unappend(count: Int): String {
  return substring(length - count, length).also {
    deleteRange(length - count, length)
  }
}

private fun List<CharSequence>.topCrates() = buildString(size) {
  this@topCrates.forEach {
    append(it.last())
  }
}