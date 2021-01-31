package search.cli

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FileIoTest {

    // Happy cases

    @Test fun testLoadingOrganizations() {
        val organizations = loadFile("Organization", "organizations")
        assertNotNull(organizations)
        assertTrue(organizations.isRight())
        organizations.map { assertNotNull(it["101"]) }
    }

    @Test fun testLoadingUsers() {
        val users = loadFile("User", "users")
        assertNotNull(users)
        assertTrue(users.isRight())
        users.map { assertNotNull(it["1"])}
    }

    @Test fun testLoadingTickets() {
        val tickets = loadFile("Ticket", "tickets")
        assertNotNull(tickets)
        assertTrue(tickets.isRight())
        tickets.map { assertNotNull(it["436bf9b0-1147-4c0a-8439-6f79833bff5b"]) }
    }

    // Unhappy cases

    // Org missing _id field
    @Test fun testLoadingInvalidOrganizations() {
        val organizations = loadFile("Organization", "organizations_invalid")
        assertTrue(organizations.isLeft())
    }

    // User missing _id field
    @Test fun testLoadingInvalidUsers() {
        val users = loadFile("User", "users_invalid")
        assertTrue(users.isLeft())
    }

    // Ticket missing _id field
    @Test fun testLoadingInvalidTickets() {
        val tickets = loadFile("Ticket", "tickets_invalid")
        assertTrue(tickets.isLeft())
    }

    // Invalid entity class name
    @Test fun testInvalidEntityType() {
        val result = loadFile("Invalid", "users")
        assert(result.isLeft())
    }

    // Invalid file name
    @Test fun testMissingFile() {
        val error = loadFile("Organization", "invalid_filename")
        assertTrue(error.isLeft())
    }
}