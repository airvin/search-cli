package search.cli

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap

fun search(
        entity: String,
        searchTerm: String,
        searchValue: String,
        organizations: List<Organization> = listOf(),
        users: List<User> = listOf(),
        tickets: List<Ticket> = listOf()
): Either<Error, List<Any>> {
    val matchingResults = when (entity) {
        "Organization" -> searchOrganizations(searchTerm, searchValue, organizations)
        "User" -> searchUsers(searchTerm, searchValue, users)
        "Ticket" -> searchTickets(searchTerm, searchValue, tickets)
        else -> Left(Error("No entity of type $entity"))
    }

    return matchingResults.flatMap {
        if (it.isEmpty()) Left(Error("No matching records for $entity $searchTerm $searchValue")) else Right(it)
    }
}

fun searchOrganizations(
        searchTerm: String,
        searchValue: String,
        organizations: List<Organization>
): Either<Error, List<Organization>> {
    return Right(organizations.filter { it._id == searchValue })
}

fun searchUsers(
        searchTerm: String,
        searchValue: String,
        users: List<User>
): Either<Error, List<User>> {
    return Right(users.filter { it._id == searchValue })
}

fun searchTickets(
        searchTerm: String,
        searchValue: String,
        tickets: List<Ticket>
): Either<Error, List<Ticket>> {
    return Right(tickets.filter { it._id == searchValue})
}

fun findEntitiesRelatedToOrg(users: List<User>, tickets: List<Ticket>, org: Organization): List<Any> {
    return listOf()
}

fun findEntitiesRelatedToUser(orgs: List<Organization>, tickets: List<Ticket>, user: User): List<Any> {
    return listOf()
}

fun findEntitiesRelatedToTicket(orgs: List<Organization>, users: List<User>, ticket: Ticket): List<Any> {
    return listOf()
}
