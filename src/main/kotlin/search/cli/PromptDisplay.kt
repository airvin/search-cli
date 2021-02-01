package search.cli

import de.m3y.kformat.Table
import de.m3y.kformat.table
import kotlin.reflect.full.memberProperties

class PromptDisplay() {
    companion object {
        val ansi_red = "\u001B[31m"
        val ansi_cyan = "\u001B[36m"
        val ansi_green = "\u001B[32m";
        val ansi_blue = "\u001B[34m";
        val ansi_purple = "\u001B[35m";
        val ansi_reset = "\u001B[0m"
    }
}
/* In order to make this application extensible, the property names that can be
searched on should not be hard coded.
Here, reflection is used to analyse the properties of the classes to create the
list of searchable fields without having to hardcode any of them.
 */
fun printSearchableFields(entity: String): String {
    val header = "${PromptDisplay.ansi_green}\n--------------------------------------\n" +
            "Search ${entity}s with \n" +
            "--------------------------------------${PromptDisplay.ansi_reset}\n"


    val entityProperties = Class.forName("search.cli.$entity").kotlin.memberProperties
            .map { it.name }
            .reduce {acc, it -> acc + "\n" + it}

    return "$header $entityProperties \n"
}


fun prettyPrintResults(
        matchedEntities: List<Entity>,
        relatedEntities: List<Pair<MutableList<String>, MutableList<String>>>,
        entityType: EntityEnum,
        relatedEntitiesOne: Map<String, Entity>,
        relatedEntitiesTwo: Map<String, Entity>,
        searchTerm: String,
        searchValue: String
) {

    val headerColour = if (matchedEntities.isEmpty()) PromptDisplay.ansi_red else PromptDisplay.ansi_cyan

    val header = if (searchValue == "NULL_OR_EMPTY") {
        "\n${headerColour}Searching ${entityType.className} " +
                "with an empty $searchTerm field returned ${matchedEntities.size} results ${PromptDisplay.ansi_reset} \n"

    } else {
        "\n${headerColour}Searching ${entityType.className} " +
                "by $searchTerm $searchValue returned ${matchedEntities.size} results ${PromptDisplay.ansi_reset} \n"
    }
    println(header)

    if (matchedEntities.isNotEmpty()) {

        when (entityType) {
            EntityEnum.ORGANIZATION -> {
                matchedEntities.mapIndexed { i, it ->
                    prettyPrintOrganization(it as Organization, i + 1)
                    prettyPrintRelatedUsers(relatedEntities[i].first.map { relatedEntitiesOne[it]!! as User })
                    prettyPrintRelatedTickets(relatedEntities[i].second.map { relatedEntitiesTwo[it]!! as Ticket })
                }
            }
            EntityEnum.USER -> {
                matchedEntities.mapIndexed { i, it ->
                    prettyPrintUser(it as User, i + 1)
                    prettyPrintRelatedOrgs(relatedEntities[i].first.map { relatedEntitiesOne[it]!! as Organization }.firstOrNull())
                    prettyPrintRelatedTickets(relatedEntities[i].second.map { relatedEntitiesTwo[it]!! as Ticket })
                }
            }
            EntityEnum.TICKET -> {
                matchedEntities.mapIndexed { i, it ->
                    prettyPrintTicket(it as Ticket, i + 1)
                    prettyPrintRelatedOrgs(relatedEntities[i].first.map { relatedEntitiesOne[it]!! as Organization }.firstOrNull())
                    prettyPrintRelatedUsers(relatedEntities[i].second.map { relatedEntitiesTwo[it]!! as User })
                }
            }
        }
    }
}

fun prettyPrintOrganization(organization: Organization, orgNum: Int) {
    println("${PromptDisplay.ansi_green}********************Organization ${orgNum}********************${PromptDisplay.ansi_reset}\n")
    val orgTable = table {
        header("Field","Value")
        row("ID", organization._id)
        row("Name", organization.name ?: "-")
        row("Url", organization.url ?: "-")
        row("Domain Names", organization.domain_names?.reduce {acc, it -> "$acc, $it"} ?: "-")
        row("External ID", organization.external_id ?: "-")
        row("Created At", organization.created_at ?: "-")
        row("Details", organization.details ?: "-")
        row("Shared Tickets", organization.shared_tickets.toString() ?: "-")
        row("Tags", organization.tags?.reduce {acc, it -> "$acc, $it"} ?: "-")
        hints {
            alignment("Field", Table.Hints.Alignment.LEFT)
            alignment("Value", Table.Hints.Alignment.LEFT)
            postfix(0, "    ")
        }
    }.render(StringBuilder())

    println(orgTable)
}

fun prettyPrintUser(user: User, userNum: Int) {
    println("${PromptDisplay.ansi_green}********************User ${userNum}********************${PromptDisplay.ansi_reset}\n")
    val userTable = table {
        header("Field", "Value")
        row("ID", user._id)
        row("Name", user.name ?: "-")
        row("Alias", user.alias ?: "-")
        row("Url", user.url ?: "-")
        row("External ID", user.external_id ?: "-")
        row("Created At", user.created_at ?: "-")
        row("Active", user.active.toString() ?: "-")
        row("Verified Tickets", user.verified.toString() ?: "-")
        row("Shared", user.shared.toString() ?: "-")
        row("Locale", user.locale ?: "-")
        row("Timezone", user.timezone ?: "-")
        row("Last Logged In At", user.last_login_at ?: "-")
        row("Email", user.email ?: "-")
        row("Phone", user.phone ?: "-")
        row("Signature", user.signature ?: "-")
        row("Organization", user.organization_id ?: "-")
        row("Tags", user.tags?.reduce {acc, it -> "$acc, $it"} ?: "-")
        row("Suspended", user.suspended.toString() ?: "-")
        row("Role", user.role ?: "-")
        hints {
            alignment("Field", Table.Hints.Alignment.LEFT)
            alignment("Value", Table.Hints.Alignment.LEFT)
            postfix(0, "    ")
            borderStyle = Table.BorderStyle.SINGLE_LINE
        }
    }.render(StringBuilder())

    println(userTable)
}

fun prettyPrintTicket(ticket: Ticket, ticketNum: Int) {
    println("${PromptDisplay.ansi_green}********************Ticket ${ticketNum}********************${PromptDisplay.ansi_reset}\n")
    val ticketTable = table {
        header("Field", "Value")
        row("ID", ticket._id)
        row("Subject", ticket.subject ?: "-")
        row("Type", ticket.type ?: "-")
        row("Url", ticket.url ?: "-")
        row("External ID", ticket.external_id ?: "-")
        row("Created At", ticket.created_at ?: "-")
        row("Priority", ticket.priority ?: "-")
        row("Status", ticket.status.toString() ?: "-")
        row("Description", ticket.description ?: "-")
        row("Submitter ID", ticket.submitter_id ?: "-")
        row("Assignee ID", ticket.assignee_id ?: "-")
        row("Organization", ticket.organization_id ?: "-")
        row("Tags", ticket.tags?.reduce {acc, it -> "$acc, $it"} ?: "-")
        hints {
            alignment("Field", Table.Hints.Alignment.LEFT)
            alignment("Value", Table.Hints.Alignment.LEFT)
            postfix(0, "    ")
            borderStyle = Table.BorderStyle.SINGLE_LINE
        }
    }.render(StringBuilder())

    println(ticketTable)
}

/*
There is only ever going to be zero or one related organizations
 */
fun prettyPrintRelatedOrgs(organization: Organization?) {

    if (organization == null) {
        println("${PromptDisplay.ansi_red}----------Error: No Related Organization----------${PromptDisplay.ansi_reset}\n")

    } else {

        println("${PromptDisplay.ansi_blue}----------Related Organization----------${PromptDisplay.ansi_reset}\n")

        // Using kformat table to build formatted table
        val orgTable = table {
            header("ID", "Name")
            row(organization._id, organization.name ?: "Anonymous")

            hints {
                alignment("ID", Table.Hints.Alignment.LEFT)
                alignment("Name", Table.Hints.Alignment.LEFT)
                borderStyle = Table.BorderStyle.SINGLE_LINE // or NONE
            }
        }.render(StringBuilder())

        println(orgTable)
    }
}


fun prettyPrintRelatedUsers(users: List<User>) {

    if (users.isEmpty()) {
        println("${PromptDisplay.ansi_red}----------No Related Users----------${PromptDisplay.ansi_reset}\n")

    } else {

        println("${PromptDisplay.ansi_blue}----------Related Users----------${PromptDisplay.ansi_reset}\n")

        // Using kformat table to build formatted table
        val userTable = table {
            header("ID", "Name", "Email")
            users.map { row(it._id, it.name ?: "Anonymous", it.email ?: "-") }

            hints {
                alignment("ID", Table.Hints.Alignment.LEFT)
                alignment("Name", Table.Hints.Alignment.LEFT)
                alignment("Email", Table.Hints.Alignment.LEFT)
                borderStyle = Table.BorderStyle.SINGLE_LINE // or NONE
            }
        }.render(StringBuilder())

        println(userTable)
    }
}


fun prettyPrintRelatedTickets(tickets: List<Ticket>) {

    if (tickets.isEmpty()) {
        println("${PromptDisplay.ansi_red}----------No Related Tickets----------${PromptDisplay.ansi_reset}\n")

    } else {

        println("${PromptDisplay.ansi_blue}----------Related Tickets----------${PromptDisplay.ansi_reset}\n")

        // Using kformat table to build formatted table
        val ticketTable = table {
            header("Subject", "Priority", "Status")
            tickets.map { row(it.subject ?: "-", it.priority ?: "-", it.status ?: "-") }
            hints {
                alignment("Subject", Table.Hints.Alignment.LEFT)
                alignment("Priority", Table.Hints.Alignment.LEFT)
                alignment("Status", Table.Hints.Alignment.LEFT)
                borderStyle = Table.BorderStyle.SINGLE_LINE
            }
        }.render(StringBuilder())

        println(ticketTable)
    }
}