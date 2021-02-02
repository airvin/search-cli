package search.cli

import arrow.core.invalid
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PromptDisplayTest {

    @Test fun testGetSearchableFields() {
        val searchableFields = getSearchableFields("Organization")
        assertTrue(searchableFields.isNotEmpty())
    }

    @Test fun testGetSearchableFieldsWithInvalidEntity() {
        val invalidEntity = getSearchableFields("Invalid Entity")
        assertTrue(invalidEntity.isNotEmpty())
        assertTrue(invalidEntity.contains("Error displaying fields. Invalid Entity is not a valid entity class"))
    }

    @Test fun testPrettyPrintOrganizationResults() {
        val organization = createMockOrganization()
        val user = createMockUser()
        val ticket = createMockTicket()
        val unit = prettyPrintResults(
                listOf(organization),
                listOf(Pair(listOf(user), listOf(ticket))),
                EntityEnum.ORGANIZATION,
                "id",
                "101")
        assertEquals(Unit, unit)
    }

    @Test fun testPrettyPrintUserResults() {
        val organization = createMockOrganization()
        val user = createMockUser()
        val ticket = createMockTicket()
        val unit = prettyPrintResults(
                listOf(user),
                listOf(Pair(listOf(organization), listOf(ticket))),
                EntityEnum.USER,
                "id",
                "1")
        assertEquals(Unit, unit)
    }

    @Test fun testPrettyPrintTicketResults() {
        val organization = createMockOrganization()
        val user = createMockUser()
        val ticket = createMockTicket()
        val unit = prettyPrintResults(
                listOf(ticket),
                listOf(Pair(listOf(organization), listOf(user))),
                EntityEnum.TICKET,
                "id",
                "00001")
        assertEquals(Unit, unit)
    }

    @Test fun testPrettyPrintOrgResultsWithNoSearchTerm() {
        val organization = createMockOrganization()
        val user = createMockUser()
        val ticket = createMockTicket()
        val unit = prettyPrintResults(
                listOf(organization),
                listOf(Pair(listOf(user), listOf(ticket))),
                EntityEnum.ORGANIZATION,
                "id",
                "NULL_OR_EMPTY")
        assertEquals(Unit, unit)
    }

    @Test fun testPrettyPrintOrganization() {
        val organization = createMockOrganization()
        val unit = prettyPrintOrganization(organization, 1)
        assertEquals(unit, Unit)
    }

    @Test fun testPrettyPrintUser() {
        val user = createMockUser()
        val unit = prettyPrintUser(user, 1)
        assertEquals(unit, Unit)
    }

    @Test fun testPrettyPrintTicket() {
        val ticket = createMockTicket()
        val unit = prettyPrintTicket(ticket, 1)
        assertEquals(unit, Unit)
    }

    @Test fun testPrettyPrintRelatedOrg() {
        val organization = createMockOrganization()
        val unit = prettyPrintRelatedOrg(organization)
        assertEquals(unit, Unit)
    }

    @Test fun testPrettyPrintRelatedOrgWithNullOrg() {
        val unit = prettyPrintRelatedOrg(null)
        assertEquals(unit, Unit)
    }

    @Test fun testPrettyPrintRelatedUser() {
        val user = createMockUser()
        val unit = prettyPrintRelatedUsers(listOf(user))
        assertEquals(unit, Unit)
    }

    @Test fun testPrettyPrintRelatedUsersWithEmptyList() {
        val unit = prettyPrintRelatedUsers(listOf())
        assertEquals(unit, Unit)
    }

    @Test fun testPrettyPrintRelatedTicket() {
        val ticket = createMockTicket()
        val unit = prettyPrintRelatedTickets(listOf(ticket))
        assertEquals(unit, Unit)
    }

    @Test fun testPrettyPrintRelatedTicketWithEmptyList() {
        val unit = prettyPrintRelatedTickets(listOf())
        assertEquals(unit, Unit)
    }
}