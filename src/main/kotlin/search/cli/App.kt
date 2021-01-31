package search.cli

import arrow.core.flatMap
import com.github.ajalt.clikt.core.CliktCommand
import kotlin.system.exitProcess

class App : CliktCommand() {
    override fun run() {

        loadFiles().flatMap { entityMaps ->

            createIndexes(entityMaps).map { indexes ->

                val organizations = entityMaps[0] as Map<String, Organization>
                val users = entityMaps[1] as Map<String, User>
                val tickets = entityMaps[2] as Map<String, Ticket>
                val (organizationIndex, userIndex, ticketIndex) = indexes
                val prompt = Prompt(organizations, users, tickets, organizationIndex, userIndex, ticketIndex)
                prompt.init()
            }

        }.mapLeft {
            val ansi_red = "\u001B[31m"
            val ansi_reset = "\u001B[0m"

            println("${ansi_red}${it.message}$ansi_reset")

            exitProcess(1)
        }
    }
}


fun main(args: Array<String>) = App().main(args)

