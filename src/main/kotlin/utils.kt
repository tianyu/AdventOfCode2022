import java.io.InputStreamReader
import java.lang.ClassLoader.getSystemResourceAsStream

fun readInput(resource: String): InputStreamReader = requireNotNull(getSystemResourceAsStream(resource)) {
  "Input not found in system resources: $resource"
}.reader()

inline fun <T> withInputLines(resource: String, action: Sequence<String>.() -> T): T =
  readInput(resource).useLines(action)

inline fun part1(action: () -> Unit) {
  println("--- Part 1 ---")
  action()
}

inline fun part2(action: () -> Unit) {
  println()
  println("--- Part 2 ---")
  action()
}

inline fun tests(action: Tests.() -> Unit) {
  println("--- Tests ---")
  Tests.action()
  println()
}

object Tests {
  inline operator fun String.invoke(test: () -> Unit) = runCatching(test)
    .onSuccess {
      println("✔   $this")
    }
    .getOrElse {
      println("✘   $this")
      print("    ")
      println(it.stackTraceToString().replace("\n", "\n    "))
    }
}