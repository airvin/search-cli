package search.cli

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import kotlin.reflect.full.memberProperties

fun search(
        searchTerm: String,
        searchValue: String,
        entityIndex: Map<String, MutableMap<String, MutableList<String>>>,
        entities: Map<String, Entity>
):  List<Entity> {
    val matchingIds = entityIndex.get(searchTerm)?.get(searchValue) ?: mutableListOf()
    return matchingIds.filter { entities[it] != null }.map { entities[it]!! }
}

fun findEntitiesRelatedToOrgs(
        userIndex: Map<String, MutableMap<String, MutableList<String>>>,
        ticketIndex: Map<String, MutableMap<String, MutableList<String>>>,
        orgs: List<Organization>
): List<Pair<MutableList<String>, MutableList<String>>> {
    return orgs.map {
        val relatedUsers = userIndex["organization_id"]!![it._id] ?: mutableListOf()
        val relatedTickets = ticketIndex["organization_id"]!![it._id] ?: mutableListOf()
        Pair(relatedUsers, relatedTickets)
    }
}

fun findEntitiesRelatedToUser(
        orgIndex: Map<String, MutableMap<String, MutableList<String>>>,
        ticketIndex: Map<String, MutableMap<String, MutableList<String>>>,
        users: List<User>
): List<Pair<MutableList<String>, MutableList<String>>> {
    return users.map {
        val relatedOrgs = orgIndex["_id"]!![it.organization_id] ?: mutableListOf()
        val relatedTicketsBySubmitterId: MutableList<String> = ticketIndex["submitter_id"]!![it._id] ?: mutableListOf()
//        val relatedTicketsByAssigneeId: MutableList<String> = ticketIndex["assignee_id"]!![it._id] ?: mutableListOf()
//        val relatedTicketsByReferrerId: MutableList<String> = ticketIndex["referrer_id"]!![it._id] ?: mutableListOf()
//        val relatedTickets: MutableList<String> = mutableListOf(relatedTicketsBySubmitterId, relatedTicketsByAssigneeId, relatedTicketsByReferrerId).flatten()
        Pair(relatedOrgs, relatedTicketsBySubmitterId)
    }
}

fun findEntitiesRelatedToTicket(
        userIndex: Map<String, MutableMap<String, MutableList<String>>>,
        orgIndex: Map<String, MutableMap<String, MutableList<String>>>,
        tickets: List<Ticket>
): List<Pair<MutableList<String>, MutableList<String>>> {
    return tickets.map {
        val relatedOrgs = orgIndex["_id"]!![it.organization_id] ?: mutableListOf()
        val relatedUsers = userIndex["_id"]!![it.submitter_id] ?: mutableListOf()
        Pair(relatedOrgs, relatedUsers)
    }
}

fun isValidSearchTerm(searchTerm: String, entity: String): Boolean =
        Class.forName("search.cli.$entity").kotlin.memberProperties.map { it.name }.contains(searchTerm)
