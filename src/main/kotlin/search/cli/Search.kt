package search.cli

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
        val relatedUsers = userIndex["organizationId"]!![it.id] ?: mutableListOf()
        val relatedTickets = ticketIndex["organizationId"]!![it.id] ?: mutableListOf()
        Pair(relatedUsers, relatedTickets)
    }
}


fun findEntitiesRelatedToUser(
        orgIndex: Map<String, MutableMap<String, MutableList<String>>>,
        ticketIndex: Map<String, MutableMap<String, MutableList<String>>>,
        users: List<User>
): List<Pair<MutableList<String>, MutableList<String>>> {
    return users.map {
        val relatedOrgs = orgIndex["id"]!![it.organizationId] ?: mutableListOf()
        val relatedTicketsBySubmitterId: MutableList<String> = ticketIndex["submitterId"]!![it.id] ?: mutableListOf()
//        val relatedTicketsByAssigneeId: MutableList<String> = ticketIndex["assigneeId"]!![it.id] ?: mutableListOf()
//        val relatedTicketsByReferrerId: MutableList<String> = ticketIndex["referrerId"]!![it.id] ?: mutableListOf()
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
        val relatedOrgs = orgIndex["id"]!![it.organizationId] ?: mutableListOf()
        val relatedUsers = userIndex["id"]!![it.submitterId] ?: mutableListOf()
        Pair(relatedOrgs, relatedUsers)
    }
}


fun isValidSearchTerm(searchTerm: String, entity: String): Boolean =
        Class.forName("search.cli.$entity").kotlin.memberProperties.map { it.name }.contains(searchTerm)
