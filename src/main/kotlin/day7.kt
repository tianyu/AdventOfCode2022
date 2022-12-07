import assertk.Assert
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.prop
import java.lang.StringBuilder

fun main() {
  tests {
    val example = """
      ${'$'} cd /
      ${'$'} ls
      dir a
      14848514 b.txt
      8504156 c.dat
      dir d
      ${'$'} cd a
      ${'$'} ls
      dir e
      29116 f
      2557 g
      62596 h.lst
      ${'$'} cd e
      ${'$'} ls
      584 i
      ${'$'} cd ..
      ${'$'} cd ..
      ${'$'} cd d
      ${'$'} ls
      4060174 j
      8033020 d.log
      5626152 d.ext
      7214296 k
    """.trimIndent().lineSequence().fileStructure()

    "Parsing file structure" {
      assertThat(example.display()).isEqualTo("""
        - / (dir)
          - a (dir)
            - e (dir)
              - i (file, size=584)
            - f (file, size=29116)
            - g (file, size=2557)
            - h.lst (file, size=62596)
          - b.txt (file, size=14848514)
          - c.dat (file, size=8504156)
          - d (dir)
            - j (file, size=4060174)
            - d.log (file, size=8033020)
            - d.ext (file, size=5626152)
            - k (file, size=7214296)
        
      """.trimIndent())
    }

    "Walking through a directory" {
      assertThat(example["/"]!!.walk().count()).isEqualTo(14)
    }

    "Get the size of a directory" {
      assertThat(example)["/", "a", "e"].isNotNull().size().isEqualTo(584)
      assertThat(example)["/", "a"].isNotNull().size().isEqualTo(94853)
      assertThat(example)["/", "d"].isNotNull().size().isEqualTo(24933642)
      assertThat(example)["/"].isNotNull().size().isEqualTo(48381165)
    }
  }

  part1("The size of all directories with total size at most 100000 is:") {
    fileStructure()["/"]!!.walk()
      .filterIsInstance<Directory>()
      .map(Directory::size)
      .filter { it <= 100000 }
      .sum()
  }

  part2("The size of the directory to delete is:") {
    val fs = fileStructure()
    val diskSize = 70_000_000
    val targetSpace = 30_000_000
    val freeSpace = diskSize - fs.size
    if (targetSpace <= freeSpace) return@part2 0
    val deleteSize = targetSpace - freeSpace
    fs.walk()
      .filterIsInstance<Directory>()
      .map(Directory::size)
      .filter { it > deleteSize }
      .min()
  }
}

private sealed interface INode {
  val size: Int
}

private class Directory: INode, MutableMap<String, INode> by mutableMapOf() {
  val children get() = entries.asSequence()
    .filterNot { (name) -> name == ".." }
    .map { (_, node) -> node }

  override val size: Int get() = children.sumOf(INode::size)

  fun display() = buildString { display(indent = "") }

  fun StringBuilder.display(indent: String) {
    this@Directory.forEach { (name, node) ->
      if (name != "..")
      append(name, node, indent)
    }
  }

  override fun toString(): String = when (val parent = get("..") as? Directory) {
    null -> ""
    else -> {
      val name = parent.entries.find { (_, inode) -> inode === this }!!.key
      "$parent$name/"
    }
  }
}

private data class File(override val size: Int): INode

private fun StringBuilder.append(name: String, node: INode, indent: String) {
  append(indent)
  append("- ")
  append(name)
  when (node) {
    is File -> appendLine(" (file, size=${node.size})")
    is Directory -> with(node) {
      appendLine(" (dir)")
      display("$indent  ")
    }
  }
}

private fun INode.walk(): Sequence<INode> = when (this) {
  is File -> sequenceOf(this)
  is Directory -> sequenceOf(this) + children.flatMap(INode::walk)
}

private fun fileStructure() = withInputLines("day7.txt", Sequence<String>::fileStructure)

private fun Sequence<String>.fileStructure(): Directory {
  val fs = Directory()
  fs["/"] = Directory()

  var currentWorkingDirectory = fs
  forEach { line ->
    when {
      line.startsWith("$ ls") -> {}
      line.startsWith("$ cd ") -> {
        val target = line.removePrefix("$ cd ")
        currentWorkingDirectory = requireNotNull(currentWorkingDirectory[target] as? Directory) {
          "Could not change directory to: $target, from: $currentWorkingDirectory"
        }
      }

      line.startsWith("dir ") -> {
        val name = line.removePrefix("dir ")
        val child = Directory()
        child[".."] = currentWorkingDirectory
        currentWorkingDirectory[name] = child
      }

      line[0].isDigit() -> {
        val (size, name) = line.split(' ')
        val child = File(size.toInt())
        currentWorkingDirectory[name] = child
      }
    }
  }
  return fs
}

private operator fun Assert<INode>.get(child: String): Assert<INode?> = isInstanceOf(Directory::class)
  .transform("$name/$child") { it[child] }

private operator fun Assert<INode>.get(vararg path: String): Assert<INode?> {
  return path.fold(this as Assert<INode?>) { current, segment ->
    current.isNotNull().get(segment)
  }
}

private fun Assert<INode>.size() = prop(INode::size)