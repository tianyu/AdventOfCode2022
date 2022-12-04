private fun main() {
  part1 {
    val maxCalories = calories().max()
    println("The elf carrying the most calories has: $maxCalories")
  }

  part2 {
    val mostCalories = calories().top(3).sum()
    println("The three elves carrying the most calories has: $mostCalories")
  }
}

private fun calories() = sequence {
  withInputLines("day1.txt") {
    var calories = 0
    val inventory = iterator()
    while (inventory.hasNext()) {
      when (val item = inventory.next()) {
        "" -> {
          yield(calories)
          calories = 0
        }
        else -> calories += item.toInt()
      }
    }
    if (calories > 0) {
      yield(calories)
    }
  }
}

private fun Sequence<Int>.top(n: Int) = IntArray(n) { 0 }.also { top ->
  forEach { value ->
    if (value > top[0]) {
      top[0] = value
      top.sort()
    }
  }
}
