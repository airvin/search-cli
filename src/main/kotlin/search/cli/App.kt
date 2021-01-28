package search.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument

class App : CliktCommand() {
    val query: String by argument(help="The search query")
    override fun run() {

        println("Searching for $query...")
    }
}

fun main(args: Array<String>) = App().main(args)

