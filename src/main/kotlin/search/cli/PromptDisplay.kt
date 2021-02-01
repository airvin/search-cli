package search.cli

import de.m3y.kformat.Table
import de.m3y.kformat.table
import kotlin.reflect.full.memberProperties

/**
 * The PromptDisplay class is used to define the ansi colour codes used in the display to highlight
 * important pieces of information.
 */
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

/**
 * getSearchableFields is used to examine an entity class and return the properties as a printable list.
 *
 * It uses reflection to find the entity class and get the list of properties for that class, without having
 * to hardcode any of them.
 *
 * @param entity    A string representing the entity class (e.g. "Organization")
 * @return      Returns a stringified list of the properties of the entity with a header.
 */
fun getSearchableFields(entity: String): String {

    val header = PromptDisplay.ansi_green +
            "\n--------------------------------------\n" +
            "  Search ${entity}s with \n" +
            "--------------------------------------\n" +
            PromptDisplay.ansi_reset


    val entityProperties = Class.forName("search.cli.$entity").kotlin.memberProperties
            .map { it.name }
            .reduce {acc, it -> acc + "\n" + it}

    return "$header $entityProperties \n"
}

/**
 * prettyPrintResulst
 */
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
        row("ID", organization.id)
        row("Name", organization.name ?: "-")
        row("Url", organization.url ?: "-")
        row("Domain Names", organization.domainNames?.reduce {acc, it -> "$acc, $it"} ?: "-")
        row("External ID", organization.externalId ?: "-")
        row("Created At", organization.createdAt ?: "-")
        row("Details", organization.details ?: "-")
        row("Shared Tickets", organization.sharedTickets.toString() ?: "-")
        row("Tags", organization.tags?.reduce {acc, it -> "$acc, $it"} ?: "-")
        hints {
            alignment("Field", Table.Hints.Alignment.LEFT)
            alignment("Value", Table.Hints.Alignment.LEFT)
            postfix(0, "    ")
            borderStyle = Table.BorderStyle.SINGLE_LINE
        }
    }.render(StringBuilder())

    println(orgTable)
}

fun prettyPrintUser(user: User, userNum: Int) {
    println("${PromptDisplay.ansi_green}********************User ${userNum}********************${PromptDisplay.ansi_reset}\n")
    val userTable = table {
        header("Field", "Value")
        row("ID", user.id)
        row("Name", user.name ?: "-")
        row("Alias", user.alias ?: "-")
        row("Url", user.url ?: "-")
        row("External ID", user.externalId ?: "-")
        row("Created At", user.createdAt ?: "-")
        row("Active", user.active.toString() ?: "-")
        row("Verified Tickets", user.verified.toString() ?: "-")
        row("Shared", user.shared.toString() ?: "-")
        row("Locale", user.locale ?: "-")
        row("Timezone", user.timezone ?: "-")
        row("Last Logged In At", user.lastLoginAt ?: "-")
        row("Email", user.email ?: "-")
        row("Phone", user.phone ?: "-")
        row("Signature", user.signature ?: "-")
        row("Organization", user.organizationId ?: "-")
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
        row("ID", ticket.id)
        row("Subject", ticket.subject ?: "-")
        row("Type", ticket.type ?: "-")
        row("Url", ticket.url ?: "-")
        row("External ID", ticket.externalId ?: "-")
        row("Created At", ticket.createdAt ?: "-")
        row("Priority", ticket.priority ?: "-")
        row("Status", ticket.status.toString() ?: "-")
        row("Description", ticket.description ?: "-")
        row("Submitter ID", ticket.submitterId ?: "-")
        row("Assignee ID", ticket.assigneeId ?: "-")
        row("Organization", ticket.organizationId ?: "-")
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
            row(organization.id, organization.name ?: "Anonymous")

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
            users.map { row(it.id, it.name ?: "Anonymous", it.email ?: "-") }

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