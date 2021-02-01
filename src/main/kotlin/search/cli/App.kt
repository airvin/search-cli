package search.cli

import arrow.core.flatMap
import com.github.ajalt.clikt.core.CliktCommand
import kotlin.system.exitProcess


fun main(args: Array<String>) = App().main(args)


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
            // If loadFiles or createIndexes has returned a Left(Error)), it means there has been
            // an unrecoverable failure, so print the error message and exit the application.
            println("${PromptDisplay.ansi_red}${it.message}${PromptDisplay.ansi_reset}")

            exitProcess(1)
        }
    }
}
