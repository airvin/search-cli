package search.cli

import arrow.core.flatMap
import com.github.ajalt.clikt.core.CliktCommand
import kotlin.system.exitProcess


fun main(args: Array<String>) = App().main(args)

/**
 * The App class is the entrypoint to the application.
 *
 * It is a subclass of CliktCommand, which is the core class of the Clikt library.
 *
 */
class App : CliktCommand() {
    /**
     * The run function first triggers the loading of all the entity json files into a
     * map of their objects. It then creates index maps for each entity type, an important
     * step to enable efficient searching. In then initialises the Prompt class, which manages the
     * prompt state machine.
     *
     * If any of the steps fail in an unrecoverable way, it will print an error message and exit.
     */
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
            println("${PromptDisplay.ansiRed}${it.message}${PromptDisplay.ansiReset}")

            exitProcess(1)
        }
    }
}
