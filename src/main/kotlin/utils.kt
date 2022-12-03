import java.io.InputStreamReader
import java.lang.ClassLoader.getSystemResourceAsStream

fun readInput(resource: String): InputStreamReader = requireNotNull(getSystemResourceAsStream(resource)) {
  "Input not found in system resources: $resource"
}.reader()

inline fun <T> withInputLines(resource: String, action: Sequence<String>.() -> T): T =
  readInput(resource).useLines(action)