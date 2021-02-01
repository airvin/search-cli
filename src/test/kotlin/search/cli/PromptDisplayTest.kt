package search.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PromptDisplayTest {

    @Test fun testGetSearchableFields() {
        val searchableFields = getSearchableFields("Organization")
        assertTrue(searchableFields.isNotEmpty())

        val invalidEntity = getSearchableFields("Invalid Entity")
        assertTrue(invalidEntity.isNotEmpty())
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