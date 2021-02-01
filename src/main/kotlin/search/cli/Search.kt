package search.cli

import kotlin.reflect.full.memberProperties

/**
 * search is used to find entities that match the search criteria.
 *
 * @param searchTerm        The entity property that was selected to search over (e.g. "id", "domainName").
 * @param searchValue       The value of the property that was entered to search (e.g. "101", "example.com").
 * @param entityIndex       The index map of the entity (e.g. Organization) that was chosen to search. This
 *                          contains a map of all the properties of the entity, which each contain an inner
 *                          map of all values, mapped to the list of ids of entities that had that property value.
 * @param entities          The map of all the entities in the application, keyed by the entity id.
 * @return                  Returns a list of entities that match the search criteria.
 */
fun search(
        searchTerm: String,
        searchValue: String,
        entityIndex: Map<String, MutableMap<String, MutableList<String>>>,
        entities: Map<String, Entity>
):  List<Entity> {
    // Lookup the entity index for the property searched on (e.g. "id") and use the searchValue
    // to find a list of corresponding entity ids that have that property value (if the key exists).
    val matchingIds = entityIndex[searchTerm]?.get(searchValue) ?: mutableListOf()

    // If matching entity ids were found, return the list of corresponding entities from the entity map.
    return matchingIds.filter { entities[it] != null }.map { entities[it]!! }
}

/**
 * findEntitiesRelatedToOrgs is used to find the users and tickets that contain the organizationId of a list
 * of organizations.
 *
 * For each organization in the orgs list, a corresponding Pair of list of Users and list of Tickets is found.
 *
 * @param orgs          The list of organizations to find the related users and tickets for.
 * @param userIndex     The map containing the properties of User as keys and maps of all the values of the
 *                      properties and corresponding user ids. This is used to efficiently find users based on
 *                      property value.
 * @param ticketIndex   The map containing the properties of Ticket as keys and maps of all the values of the
 *                      properties and corresponding ticket ids. This is used to efficiently find tickets based on
 *                      property value.
 * @param users         The map of user id to User used to efficiently find the User from its id.
 * @param tickets       The map of ticket id to Ticket used to efficiently find the Ticket from its id.
 * @return              Returns a list of pairs. Each pair contains a list of users and list of tickets that
 *                      correspond to the organization at the same index in the orgs list.
 */
fun findEntitiesRelatedToOrgs(
        orgs: List<Organization>,
        userIndex: Map<String, MutableMap<String, MutableList<String>>>,
        ticketIndex: Map<String, MutableMap<String, MutableList<String>>>,
        users: Map<String, User>,
        tickets: Map<String, Ticket>
): List<Pair<List<User>, List<Ticket>>> = orgs.map { org ->

    // Find any users that have a matching organizationId of the org
    val relatedUserIds = userIndex["organizationId"]!![org.id]  ?: mutableListOf()
    // Find the corresponding User from the user ids found
    val relatedUsers = relatedUserIds.map { userId -> users[userId]!! }

    // Find any tickets that have a matching organizationId of the org
    val relatedTicketIds = ticketIndex["organizationId"]!![org.id] ?: mutableListOf()
    // Find the corresponding Ticket from the ticket ids found
    val relatedTickets = relatedTicketIds.map { ticketId -> tickets[ticketId]!! }

    Pair(relatedUsers, relatedTickets)
}


/**
 * findEntitiesRelatedToUsers is used to find the organization and tickets that correspond to a user in a list of users.
 *
 * For each user in the users list, a corresponding Pair of list of Organizations and list of Tickets is found.
 *
 * @param users         The list of users to find the related organizations and tickets for.
 * @param ticketIndex   The map containing the properties of Ticket as keys and maps of all the values of the
 *                      properties and corresponding ticket ids. This is used to efficiently find tickets based on
 *                      property value.
 * @param orgs          The map of org id to Organization used to efficiently find the Organization from its id.
 * @param tickets       The map of ticket id to Ticket used to efficiently find the Ticket from its id.
 * @return              Returns a list of pairs. Each pair contains a list of Organizations and list of Tickets that
 *                      correspond to the user at the same index in the users list.
 */
fun findEntitiesRelatedToUsers(
        users: List<User>,
        orgIndex: Map<String, MutableMap<String, MutableList<String>>>,
        ticketIndex: Map<String, MutableMap<String, MutableList<String>>>,
        orgs: Map<String, Organization>,
        tickets: Map<String, Ticket>
): List<Pair<List<Organization>, List<Ticket>>> = users.map { user ->

    // Lookup the organization in the orgs map using the user's organizationId. Note
    // that user.organizationId may be null, in which case an empty list is returned.
    val relatedOrg = listOfNotNull(orgs[user.organizationId])

    // Find the tickets that were submitted, assigned or requested by the user
    val relatedTicketIds = ticketIndex["submitterId"]!![user.id] ?: mutableListOf()
    relatedTicketIds.addAll(ticketIndex["assigneeId"]!![user.id] ?: mutableListOf())
    relatedTicketIds.addAll(ticketIndex["requesterId"]!![user.id] ?: mutableListOf())
    // Find the corresponding Ticket from the ticket ids found
    val relatedTickets = relatedTicketIds.map { ticketId -> tickets[ticketId]!! }

    Pair(relatedOrg, relatedTickets)
}


/**
 * findEntitiesRelatedToTickets is used to find the organization and users that correspond to a ticket
 * in a list of tickets.
 *
 * For each ticket in the tickets list, a corresponding Pair of list of Organizations and list of Tickets is found.
 *
 * @param tickets       The list of tickets to find the related organizations and users for.
 * @param orgIndex      The map containing the properties of Organization as keys and maps of all the values of the
 *                      properties and corresponding organization ids. This is used to efficiently find organizations
 *                      based on property value.
 * @param userIndex     The map containing the properties of User as keys and maps of all the values of the
 *                      properties and corresponding user ids. This is used to efficiently find users based on
 *                      property value.
 * @param orgs          The map of org id to Organization used to efficiently find the Organization from its id.
 * @param users         The map of user id to User used to efficiently find the User from its id.
 * @return              Returns a list of pairs. Each pair contains an Organization and list of Users that
 *                      correspond to the ticket at the same index in the ticket list.
 */
fun findEntitiesRelatedToTickets(
        tickets: List<Ticket>,
        orgs: Map<String, Organization>,
        users: Map<String, User>
): List<Pair<List<Organization>, List<User>>> = tickets.map { ticket ->

    // Lookup the organization in the orgs map using the ticket's organizationId. Note
    // that ticket.organizationId may be null, in which case an empty list is returned.
    val relatedOrgs = listOfNotNull(orgs[ticket.organizationId])

    // Find the users that submitted, assigned or requested the ticket
    val submitter = users[ticket.submitterId]
    val assignee = users[ticket.assigneeId]
    val requester = users[ticket.requesterId]
    val relatedUsers = listOfNotNull(submitter, assignee, requester)

    Pair(relatedOrgs, relatedUsers)
}

/**
 * isValidSearchTerm uses reflection to determine whether the provided search term is one of the property
 * names of the class corresponding to the provided entity.
 *
 * As no property information relating to the entity classes are hardcoded in this function, it does not
 * need to be updated if new entities are added or if properties change.
 *
 * @param searchTerm        The search term (e.g. "id" or "url") provided by the user that should be one of the
 *                          property names of the entity class.
 * @param entity            A string representing an entity class (e.g. "Organization")
 * @return                  Returns true if the search term matches one of the entity's property names, or false.
 */
fun isValidSearchTerm(searchTerm: String, entity: String): Boolean = try {
    Class.forName("search.cli.$entity").kotlin.memberProperties.map { it.name }.contains(searchTerm)
} catch (e: ClassNotFoundException) {
    println("Error: $entity class not found")
    false
}
