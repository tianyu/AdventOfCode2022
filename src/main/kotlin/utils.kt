import java.io.InputStreamReader
import java.lang.ClassLoader.getSystemResourceAsStream
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

fun inputStream(resource: String) = requireNotNull(getSystemResourceAsStream(resource)) {
  "Input not found in system resources: $resource"
}

fun readInput(resource: String): InputStreamReader = inputStream(resource).reader()

inline fun <T> withInputLines(resource: String, action: Sequence<String>.() -> T): T =
  readInput(resource).useLines(action)

@OptIn(ExperimentalTime::class)
inline fun part1(description: String, action: () -> Any) {
  println("--- Part 1 ---")
  val (result, duration) = measureTimedValue(action)
  print(description)
  print(' ')
  println(result)
  println("Completed in $duration")
}

@OptIn(ExperimentalTime::class)
inline fun part2(description: String, action: () -> Any) {
  println()
  println("--- Part 2 ---")
  val (result, duration) = measureTimedValue(action)
  print(description)
  if (!description.last().isWhitespace()) {
    print(' ')
  }
  println(result)
  println("Completed in $duration")
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