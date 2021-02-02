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
        val ansiRed = "\u001B[31m"
        val ansiCyan = "\u001B[36m"
        val ansiGreen = "\u001B[32m"
        val ansiBlue = "\u001B[34m"
        val ansiReset = "\u001B[0m"
    }
}

/**
 * getSearchableFields is used to examine an entity class and return the properties as a printable list.
 *
 * It uses reflection to find the entity class and get the list of properties for that class, without having
 * to hardcode them. Therefore, if new entities are added or if properties are changed, this function
 * does not need to be updated.
 *
 * @param entity    A string representing the entity class (e.g. "Organization")
 * @return      Returns a stringified list of the properties of the entity with a header.
 */
fun getSearchableFields(entity: String): String = try {

    val header = PromptDisplay.ansiGreen +
            "\n--------------------------------------\n" +
            "  Search ${entity}s with \n" +
            "--------------------------------------\n" +
            PromptDisplay.ansiReset

    val entityProperties = Class.forName("search.cli.$entity").kotlin.memberProperties
            .map { it.name }
            .reduce {acc, it -> acc + "\n" + it}

    header + entityProperties + "\n"
} catch (e: ClassNotFoundException) {
    PromptDisplay.ansiRed + "Error displaying fields. $entity is not a valid entity class\n" + PromptDisplay.ansiReset
}

/**
 * prettyPrintResults is used to print the results of the search.
 *
 * The list of entities found from the search are supplied in a list, and the list of pairs of related entities are
 * supplied in another list with the relationship between the entities in the two lists defined by their index.
 *
 * @param matchedEntities       This is the list of entities (e.g. Organizations) that match the search result directly.
 * @param relatedEntities       A list of pairs of related entities. Each list of pairs corresponds to the entity in the
 *                              `matchedEntities` list at the same index. For example the if the matched entities are
 *                              Organizations, the first Organization in `matchedEntities` is related to the list of
 *                              Users and Tickets in the pair at the first position in the `relatedEntities` list.
 * @param entityType            This is the type of entity that the search was performed on (e.g. "Organization").
 * @param searchTerm            The entity property that the user searched (e.g. "id" or "domainName").
 * @param searchValue           The value that the user searched (e.g. "101" or "example.com").
 */
fun prettyPrintResults(
        matchedEntities: List<Entity>,
        relatedEntities: List<Pair<List<Entity>, List<Entity>>>,
        entityType: EntityEnum,
        searchTerm: String,
        searchValue: String
) {

    val headerColour = if (matchedEntities.isEmpty()) PromptDisplay.ansiRed else PromptDisplay.ansiCyan

    val header = if (searchValue == "NULL_OR_EMPTY") {
            headerColour +
            "\nSearching ${entityType.className} with an empty " +
            "$searchTerm field returned ${matchedEntities.size} results\n" +
            PromptDisplay.ansiReset

    } else {
            headerColour +
            "\nSearching ${entityType.className} by $searchTerm " +
            "$searchValue returned ${matchedEntities.size} results \n" +
            PromptDisplay.ansiReset
    }
    println(header)

    if (matchedEntities.isNotEmpty()) {

        // Each of the entities returned directly as a result of the search need to be printed in full
        // with one of the prettyPrintOrganization, prettyPrintUser, prettyPrintTicket functions.
        // Related entities are printed with prettyPrintRelatedOrg, prettyPrintRelatedUsers, prettyPrintRelatedTickets.
        when (entityType) {
            EntityEnum.ORGANIZATION -> {
                matchedEntities.mapIndexed { i, it ->
                    prettyPrintOrganization(it as Organization, i + 1)
                    prettyPrintRelatedUsers(relatedEntities[i].first as List<User>)
                    prettyPrintRelatedTickets(relatedEntities[i].second as List<Ticket>)
                }
            }
            EntityEnum.USER -> {
                matchedEntities.mapIndexed { i, it ->
                    prettyPrintUser(it as User, i + 1)
                    prettyPrintRelatedOrg(relatedEntities[i].first.firstOrNull() as Organization?)
                    prettyPrintRelatedTickets(relatedEntities[i].second as List<Ticket>)
                }
            }
            EntityEnum.TICKET -> {
                matchedEntities.mapIndexed { i, it ->
                    prettyPrintTicket(it as Ticket, i + 1)
                    prettyPrintRelatedOrg(relatedEntities[i].first.firstOrNull() as Organization?)
                    prettyPrintRelatedUsers(relatedEntities[i].second as List<User>)
                }
            }
        }
    }
}


/**
 * prettyPrintOrganization is used to print organizations when they are the entity
 * that was being searched by the user.
 *
 * The printed header includes the index of the organization as it appeared in the matchedEntities
 * search results list. All fields of the organization are printed in a table. As all properties of
 * Organization are nullable (apart from id), a null check is performed on each field (the "?:")
 * and if the field is null, a dash is printed instead.
 *
 * @param organization      The organization to print
 * @param orgNum            The number of this organization as it appeared in the search result list
 */
fun prettyPrintOrganization(organization: Organization, orgNum: Int) {

    println(PromptDisplay.ansiGreen +
            "********************Organization ${orgNum}********************\n" +
            PromptDisplay.ansiReset)

    // Using kformat table to build formatted table
    val orgTable = table {
        header("Field","Value")
        row("ID", organization.id)
        row("Name", organization.name ?: "-")
        row("Url", organization.url ?: "-")
        row("Domain Names", organization.domainNames?.reduce {acc, it -> "$acc, $it"} ?: "-")
        row("External ID", organization.externalId ?: "-")
        row("Created At", organization.createdAt ?: "-")
        row("Details", organization.details ?: "-")
        row("Shared Tickets", organization.sharedTickets?.toString() ?: "-")
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


/**
 * prettyPrintUser is used to print users when they are the entity
 * that was being searched by the user.
 *
 * The printed header includes the index of the user as it appeared in the matchedEntities
 * search results list. All fields of the user are printed in a table. As all properties of
 * User are nullable (apart from id), a null check is performed on each field (the "?:")
 * and if the field is null, a dash is printed instead.
 *
 * @param user              The user to print
 * @param userNum           The number of this user as it appeared in the search result list
 */
fun prettyPrintUser(user: User, userNum: Int) {

    println(PromptDisplay.ansiGreen +
            "********************User ${userNum}********************\n" +
            PromptDisplay.ansiReset)

    // Using kformat table to build formatted table
    val userTable = table {
        header("Field", "Value")
        row("ID", user.id)
        row("Name", user.name ?: "-")
        row("Alias", user.alias ?: "-")
        row("Url", user.url ?: "-")
        row("External ID", user.externalId ?: "-")
        row("Created At", user.createdAt ?: "-")
        row("Active", user.active?.toString() ?: "-")
        row("Verified Tickets", user.verified?.toString() ?: "-")
        row("Shared", user.shared?.toString() ?: "-")
        row("Locale", user.locale ?: "-")
        row("Timezone", user.timezone ?: "-")
        row("Last Logged In At", user.lastLoginAt ?: "-")
        row("Email", user.email ?: "-")
        row("Phone", user.phone ?: "-")
        row("Signature", user.signature ?: "-")
        row("Organization", user.organizationId ?: "-")
        row("Tags", user.tags?.reduce {acc, it -> "$acc, $it"} ?: "-")
        row("Suspended", user.suspended?.toString() ?: "-")
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


/**
 * prettyPrintTicket is used to print tickets when they are the entity
 * that was being searched by the user.
 *
 * The printed header includes the index of the ticket as it appeared in the matchedEntities
 * search results list. All fields of the ticket are printed in a table. As all properties of
 * Ticket are nullable (apart from id), a null check is performed on each field (the "?:")
 * and if the field is null, a dash is printed instead.
 *
 * @param ticket              The ticket to print
 * @param ticketNum           The number of this ticket as it appeared in the search result list
 */
fun prettyPrintTicket(ticket: Ticket, ticketNum: Int) {

    println(PromptDisplay.ansiGreen +
            "********************Ticket ${ticketNum}********************\n" +
            PromptDisplay.ansiReset)

    // Using kformat table to build formatted table
    val ticketTable = table {
        header("Field", "Value")
        row("ID", ticket.id)
        row("Subject", ticket.subject ?: "-")
        row("Type", ticket.type ?: "-")
        row("Url", ticket.url ?: "-")
        row("External ID", ticket.externalId ?: "-")
        row("Created At", ticket.createdAt ?: "-")
        row("Priority", ticket.priority ?: "-")
        row("Status", ticket.status?.toString() ?: "-")
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


/**
 * prettyPrintRelatedOrg prints an Organization that is related to a User or a Ticket.
 *
 * An error is printed if the organization is missing as each user or ticket should be associated
 * with exactly one organization.
 *
 * @param organization      The organization to print. If this parameter is null, it means the related
 *                          user or ticket was missing the organizationId field.
 */
fun prettyPrintRelatedOrg(organization: Organization?) {

    if (organization == null) {
        println(PromptDisplay.ansiRed +
                "----------Error: No Related Organization----------\n" +
                PromptDisplay.ansiReset)

    } else {
        println(PromptDisplay.ansiBlue +
                "----------Related Organization----------\n" +
                PromptDisplay.ansiReset)

        val orgTable = table {
            header("ID", "Name")
            row(organization.id, organization.name ?: "-")

            hints {
                alignment("ID", Table.Hints.Alignment.LEFT)
                alignment("Name", Table.Hints.Alignment.LEFT)
                borderStyle = Table.BorderStyle.SINGLE_LINE // or NONE
            }
        }.render(StringBuilder())

        println(orgTable)
    }
}


/**
 * prettyPrintRelatedUsers prints a list of users that are related to an organization or ticket.
 *
 * If the list is empty, a message saying there are no related users is displayed. Otherwise a table
 * with the user id, name and email is displayed.
 *
 * @param users      A list of users to print. May be an empty list if there were no related users.
 */
fun prettyPrintRelatedUsers(users: List<User>) {

    if (users.isEmpty()) {
        println(PromptDisplay.ansiRed +
                "----------No Related Users----------\n" +
                PromptDisplay.ansiReset)

    } else {

        println(PromptDisplay.ansiBlue +
                "----------Related Users----------\n" +
                PromptDisplay.ansiReset)

        // Using kformat table to build formatted table
        val userTable = table {
            header("ID", "Name", "Email")

            // For each user in the list, create a row with the user id, name and email
            users.map { row(it.id, it.name ?: "-", it.email ?: "-") }

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


/**
 * prettyPrintRelatedTickets prints a list of tickets that are related to an organization or user.
 *
 * If the list is empty, a message saying there are no related tickets is displayed. Otherwise a table
 * with the ticket subject, priority and status is displayed.
 *
 * @param tickets      A list of tickets to print. May be an empty list if there were no related tickets.
 */
fun prettyPrintRelatedTickets(tickets: List<Ticket>) {

    if (tickets.isEmpty()) {
        println(PromptDisplay.ansiRed +
                "----------No Related Tickets----------\n" +
                PromptDisplay.ansiReset)

    } else {
        println(PromptDisplay.ansiBlue +
                "----------Related Tickets----------\n" +
                PromptDisplay.ansiReset)

        // Using kformat table to build formatted table
        val ticketTable = table {
            header("Subject", "Priority", "Status")

            // For each ticket in the list, create a row with the ticket subject, priority and status
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