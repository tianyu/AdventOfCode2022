import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo

private fun main() {
  tests {
    "Parse monkeys" {
      val monkey = monkeys("day11-example.txt")[0]
      assertThat(monkey.items).containsExactly(79.toWorry(), 98.toWorry())
      assertThat(monkey.inspect(10.toWorry())).isEqualTo(190.toWorry())
      assertThat(monkey.divisor).isEqualTo(23.toWorry())
      assertThat(monkey.ifDivisible).isEqualTo(2)
      assertThat(monkey.ifNotDivisible).isEqualTo(3)
    }

    "One round of monkey business" {
      val monkeys = monkeys("day11-example.txt")
      monkeys.forEach { monkey -> monkey.inspectItems(monkeys) { it / 3.toWorry() } }
      assertThat(monkeys[0].items).containsExactly(20.toWorry(), 23.toWorry(), 27.toWorry(), 26.toWorry())
      assertThat(monkeys[1].items).containsExactly(2080.toWorry(), 25.toWorry(), 167.toWorry(), 207.toWorry(), 401.toWorry(), 1046.toWorry())
      assertThat(monkeys[2].items).isEmpty()
      assertThat(monkeys[3].items).isEmpty()
    }

    "Part 1 Example" {
      assertThat(monkeys("day11-example.txt").monkeyBusiness(20) { it / 3.toWorry() })
        .containsExactly(101, 95, 7, 105)
    }

    "Part 2 Examples" {
      assertThat(monkeys("day11-example.txt").monkeyBusiness(1))
        .containsExactly(2, 4, 3, 6)
      assertThat(monkeys("day11-example.txt").monkeyBusiness(20))
        .containsExactly(99, 97, 8, 103)
      assertThat(monkeys("day11-example.txt").monkeyBusiness(1000))
        .containsExactly(5204, 4792, 199, 5192)
      assertThat(monkeys("day11-example.txt").monkeyBusiness(10000))
        .containsExactly(52166, 47830, 1938, 52013)
    }
  }

  part1("The level of monkey business after 20 rounds is:") {
    val (top1, top2) = monkeys().monkeyBusiness(20) { it / 3.toWorry() }.apply(LongArray::sortDescending)
    top1 * top2
  }

  part2("The level of monkey business after 10000 rounds is:") {
    val (top1, top2) = monkeys().monkeyBusiness(10000).apply(LongArray::sortDescending)
    top1 * top2
  }
}

private fun List<Monkey>.noComfort(): (Worry) -> Worry {
  val mod = fold(1.toWorry()) { product, monkey -> product * monkey.divisor }
  return { it % mod }
}

private fun List<Monkey>.monkeyBusiness(rounds: Int, comfort: (Worry) -> Worry = noComfort()): LongArray {
  val inspections = LongArray(size)
  repeat(rounds) {
    forEachIndexed { index, monkey ->
      inspections[index] += monkey.items.size.toLong()
      monkey.inspectItems(this, comfort)
    }
  }
  return inspections
}

private typealias Worry = Long
private fun String.toWorry(): Worry = toLong()
private fun Int.toWorry(): Worry = toLong()

private data class Monkey(
  val items: MutableList<Worry>,
  val inspect: (Worry) -> Worry,
  val divisor: Worry,
  val ifDivisible: Int,
  val ifNotDivisible: Int,
) {
  fun inspectItems(monkeys: List<Monkey>, comfort: (Worry) -> Worry) {
    for (item in items) {
      val worry = comfort(inspect(item))
      val target = if (worry % divisor == 0.toWorry()) ifDivisible else ifNotDivisible
      monkeys[target].items += worry
    }
    items.clear()
  }
}

private fun monkeys(resource: String = "day11.txt") = withInputLines(resource) {
  chunked(7) { (_, startingItems, operation, test, ifTrue, ifFalse) ->
    Monkey(
      startingItems.removePrefix("  Starting items: ").asItems(),
      operation.removePrefix("  Operation: new = ").asOperation(),
      test.removePrefix("  Test: divisible by ").toWorry(),
      ifTrue.removePrefix("    If true: throw to monkey ").toInt(),
      ifFalse.removePrefix("    If false: throw to monkey ").toInt(),
    )
  }.toList()
}

private fun String.asItems(): MutableList<Worry> =
  splitToSequence(", ").mapTo(mutableListOf(), String::toWorry)

private fun String.asOperation(): (Worry) -> Worry {
  if (this == "old") return { it }
  if (all(Char::isDigit)) {
    val const = toWorry()
    return { const }
  }
  val (left, operator, right) = split(' ')
  val leftOperation = left.asOperation()
  val rightOperation = right.asOperation()
  return when (operator) {
    "+" -> {{ leftOperation(it) + rightOperation(it) }}
    "*" -> {{ leftOperation(it) * rightOperation(it) }}
    else -> throw AssertionError("Unknown operator: $operator")
  }
}