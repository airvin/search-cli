package search.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.output.TermUi

class App : CliktCommand() {
    override fun run() {
        // TODO: load data from files
        
        promptStateMachineInit()
    }
}

fun main(args: Array<String>) = App().main(args)

