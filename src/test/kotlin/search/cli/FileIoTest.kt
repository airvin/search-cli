package search.cli

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FileIoTest {
    @Test fun testLoadingOrganizations() {
        val organizations = loadFile("Organization", "organizations") as Map<String, Organization>
        assertNotNull(organizations)
        assertTrue(!organizations.isEmpty())
        assertNotNull(organizations["101"])
    }

    @Test fun testLoadingInvalidOrganizations() {
        val organizations = loadFile("Organization", "organizations_invalid") as Map<String, Organization>
        assertTrue(organizations.isEmpty())
    }

    @Test fun testLoadingTickets() {
        val tickets = loadFile("Ticket", "tickets") as Map<String, Ticket>
        assertNotNull(tickets)
        assertTrue(!tickets.isEmpty())
    }

    @Test fun testLoadingInvalidTickets() {
        val tickets = loadFile("Ticket", "tickets_invalid") as Map<String, Ticket>
        assertTrue(tickets.isEmpty())
    }

    @Test fun testLoadingUsers() {
        val users = loadFile("User", "users") as Map<String, User>
        assertNotNull(users)
        assertTrue(!users.isEmpty())
    }

    @Test fun testLoadingInvalidUsers() {
        val users = loadFile("User", "users_invalid") as Map<String, User>
        assertTrue(users.isEmpty())
    }

    @Test fun testMissingFile() {
        val error = loadFile("Organization", "invalidi_filename") as Map<String, Organization>
        assertTrue(error.isEmpty())
    }
}