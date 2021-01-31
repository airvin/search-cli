package search.cli

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap

fun search(
        searchTerm: String,
        searchValue: String,
        entityIndex: Map<String, MutableMap<String, MutableList<String>>>,
        entities: Map<String, Entity>
):  List<Entity> {
    val matchingIds = entityIndex.get(searchTerm)?.get(searchValue) ?: mutableListOf()
    return matchingIds.filter { entities[it] != null }.map { entities[it]!! }
}

fun findEntitiesRelatedToOrg(
        userIndex: Map<String, MutableMap<String, MutableList<String>>>,
        users: Map<String, Entity>,
        ticketIndex: Map<String, MutableMap<String, MutableList<String>>>,
        tickets: Map<String, Entity>,
        org: Organization
): List<Entity> {
    return listOf()
}

fun findEntitiesRelatedToUser(
        orgIndex: Map<String, MutableMap<String, MutableList<String>>>,
        orgs: Map<String, Entity>,
        ticketIndex: Map<String, MutableMap<String, MutableList<String>>>,
        tickets: Map<String, Entity>,
        user: User
): List<Entity> {
    return listOf()
}

fun findEntitiesRelatedToTicket(
        orgIndex: Map<String, MutableMap<String, MutableList<String>>>,
        orgs: Map<String, Entity>,
        userIndex: Map<String, MutableMap<String, MutableList<String>>>,
        users: Map<String, Entity>,
        ticket: Ticket
): List<Entity> {
    return listOf()
}
