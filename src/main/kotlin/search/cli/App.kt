package search.cli

import arrow.core.Either
import arrow.core.Right
import arrow.core.flatMap
import com.github.ajalt.clikt.core.CliktCommand

class App : CliktCommand() {

    val organizations = loadFile("Organization", "organizations") as Map<String, Organization>
    val users = loadFile("User", "users") as Map<String, User>
    val tickets = loadFile("Ticket", "tickets") as Map<String, Ticket>
    val organizationIndex = createIndex("Organization", organizations)
    val userIndex = createIndex("User", users)
    val ticketIndex = createIndex("Ticket", tickets)

    val prompt = Prompt(organizations, users, tickets, organizationIndex, userIndex, ticketIndex)

    override fun run() {

        prompt.init()
    }
}

fun main(args: Array<String>) = App().main(args)

