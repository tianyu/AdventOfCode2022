import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo

private fun main() {
  tests {
    "Reading signals" {
      assertThat("""
        noop
        addx 3
        addx -5
        
      """.trimIndent().lineSequence().signals().toList()).containsExactly(
        1,
        1, 1,
        4, 4,
      )
    }

    "Signal strengths" {
      assertThat(signals("day10-example.txt").signalStrengths().toList()).containsExactly(
        420, 1140, 1800, 2940, 2880, 3960
      )
    }

    "Drawing signals" {
      assertThat(signals("day10-example.txt").draw()).isEqualTo("""
        ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  
        ###   ###   ###   ###   ###   ###   ### 
        ####    ####    ####    ####    ####    
        #####     #####     #####     #####     
        ######      ######      ######      ####
        #######       #######       #######     

      """.trimIndent())
    }
  }

  part1("The total signal strength is:") {
    signals().signalStrengths().sum()
  }

  part2("The CRT reads:\n") {
    signals().draw()
  }
}

private fun Sequence<String>.signals() = sequence {
  var x = 1
  for (instruction in this@signals) {
    when {
      instruction == "noop" -> yield(x)
      instruction.startsWith("addx ") -> {
        yield(x)
        yield(x)
        x += instruction.substring(5).toInt()
      }
    }
  }
}

private fun Sequence<Int>.signalStrengths() = sequence {
  var cycle = 0
  for (signal in this@signalStrengths) {
    cycle += 1
    if ((cycle - 20) % 40 == 0) {
      yield(cycle * signal)
    }
  }
}

private fun signals(resource: String = "day10.txt") = sequence {
  withInputLines(resource) {
    yieldAll(signals())
  }
}

private fun Sequence<Int>.draw() = buildString {
  var cursor = 0
  for (x in this@draw) {
    val sprite = x - 1..x + 1
    append(when (cursor) {
      in sprite -> '#'
      else -> ' '
    })
    cursor += 1
    if (cursor == 40) {
      cursor = 0
      appendLine()
    }
  }
}