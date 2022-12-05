import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo

private fun main() {
  val crateMover9000 = CargoCrane { stacks, count, from, to ->
    stacks[to].append(stacks[from].unappend(count).reversed())
  }

  val crateMover9001 = CargoCrane { stacks, count, from, to ->
    stacks[to].append(stacks[from].unappend(count))
  }

  tests {
    "Popping off the top of the stack" {
      val stringBuilder = StringBuilder("abcdef")
      assertThat(stringBuilder.unappend(1)).isEqualTo("f")
      assertThat(stringBuilder.toString()).isEqualTo("abcde")
      assertThat(stringBuilder.unappend(3)).isEqualTo("cde")
      assertThat(stringBuilder.toString()).isEqualTo("ab")
    }

    "Getting the top of each stack" {
      val stacks = listOf(
        StringBuilder("abcde"),
        StringBuilder("abc"),
        StringBuilder("abcdef"),
      )

      assertThat(stacks.topCrates()).isEqualTo("ecf")
    }

    "Reading stacks" {
      val stack = """
              [D]    
          [N] [C]    
          [Z] [M] [P]
           1   2   3 
        """.trimIndent()

      assertThat(stack.lines().iterator().readStacks().map(StringBuilder::toString))
        .containsExactly("ZN", "MCD", "P")
    }

    val example = """
            [D]    
        [N] [C]    
        [Z] [M] [P]
         1   2   3 

        move 1 from 2 to 1
        move 3 from 1 to 3
        move 2 from 2 to 1
        move 1 from 1 to 2
      """.trimIndent()

    "Part 1 Example" {
      assertThat(crateMover9000.moveCrates(example.lineSequence()).map(CharSequence::toString))
        .containsExactly("C", "M", "PDNZ")
    }

    "Part 2 Example" {
      assertThat(crateMover9001.moveCrates(example.lineSequence()).map(CharSequence::toString))
        .containsExactly("M", "C", "PZND")
    }
  }

  part1 {
    val topCrates = crateMover9000.moveCrates().topCrates()
    println("Using the CrateMover9000, the crates at the top are: $topCrates")
  }

  part2 {
    val topCrates = crateMover9001.moveCrates().topCrates()
    println("Using the CrateMover9001, the crates at the top are: $topCrates")
  }
}

private fun interface CargoCrane {
  fun move(stacks: List<StringBuilder>, count: Int, from: Int, to: Int)
}

private fun CargoCrane.moveCrates(): List<CharSequence> = withInputLines("day5.txt", this::moveCrates)

private fun CargoCrane.moveCrates(input: Sequence<String>): List<CharSequence> {
  val lines = input.iterator()
  val stacks = lines.readStacks()

  assert(lines.next() == "")

  for (line in lines) {
    if (!line.startsWith("move ")) break
    val (count, from, to) = line.split(' ').mapNotNull(String::toIntOrNull)
    move(stacks, count, from - 1, to - 1)
  }

  return stacks
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