package search.cli

import arrow.core.Either
import arrow.core.Right
import arrow.core.flatMap
import com.github.ajalt.clikt.core.CliktCommand

class App : CliktCommand() {


    val organizations = loadFile("Organization", "organizations") as List<Organization>
    val users = loadFile("User", "users") as List<User>
    val tickets = loadFile("Ticket", "tickets") as List<Ticket>

    override fun run() {
        val prompt = PromptStateMachine(organizations, users, tickets)
        prompt.init()
    }
}

fun main(args: Array<String>) = App().main(args)

