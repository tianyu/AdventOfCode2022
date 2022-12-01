import java.io.InputStreamReader
import java.lang.ClassLoader.getSystemResourceAsStream

fun readInput(resource: String): InputStreamReader = requireNotNull(getSystemResourceAsStream(resource)) {
  "Input not found in system resources: $resource"
}.reader()