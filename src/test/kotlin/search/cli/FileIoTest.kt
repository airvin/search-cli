package search.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FileIoTest {

    // Happy cases

    @Test fun testLoadingFiles() {
        val eitherErrorEntityMaps = loadFiles()
        assertTrue(eitherErrorEntityMaps.isRight())
        eitherErrorEntityMaps.map {
            assertTrue(it.isNotEmpty())

            // Organization map tests
            assertTrue(it[0].isNotEmpty())
            assertNotNull(it[0]["101"])
            assertTrue(it[0]["101"] is Organization)
            assertEquals( "Enthaze", (it[0]["101"] as Organization).name)

            // User map tests
            assertTrue(it[1].isNotEmpty())
            assertNotNull(it[1]["1"])
            assertTrue(it[1]["1"] is User)
            assertEquals("Francisca Rasmussen", (it[1]["1"] as User).name)

            // Ticket map tests
            assertTrue(it[2].isNotEmpty())
            assertNotNull(it[2]["436bf9b0-1147-4c0a-8439-6f79833bff5b"])
            assertTrue(it[2]["436bf9b0-1147-4c0a-8439-6f79833bff5b"] is Ticket)
            assertEquals(
                    "A Catastrophe in Korea (North)",
                    (it[2]["436bf9b0-1147-4c0a-8439-6f79833bff5b"] as Ticket).subject)
        }
    }

    @Test fun testLoadingOrganizations() {
        val organizations = loadFile("Organization", "organizations")
        assertNotNull(organizations)
        assertTrue(organizations.isRight())
        organizations.map {
            assertNotNull(it["102"])
            assertTrue(it["102"] is Organization)
            assertEquals("Nutralab", (it["102"] as Organization).name)
        }
    }

    @Test fun testLoadingUsers() {
        val users = loadFile("User", "users")
        assertNotNull(users)
        assertTrue(users.isRight())
        users.map {
            assertNotNull(it["2"])
            assertTrue(it["2"] is User)
            assertEquals("Miss Joni", (it["2"] as User).alias)
        }
    }

    @Test fun testLoadingTickets() {
        val tickets = loadFile("Ticket", "tickets")
        assertNotNull(tickets)
        assertTrue(tickets.isRight())
        tickets.map {
            assertNotNull(it["1a227508-9f39-427c-8f57-1b72f3fab87c"])
            assertTrue(it["1a227508-9f39-427c-8f57-1b72f3fab87c"] is Ticket)
            assertEquals(
                    "A Catastrophe in Micronesia",
                    (it["1a227508-9f39-427c-8f57-1b72f3fab87c"] as Ticket).subject)
        }
    }

    // Unhappy cases

    // Org missing _id field
    @Test fun testLoadingInvalidOrganizations() {
        val organizationsError = loadFile("Organization", "organizations_invalid")
        assertTrue(organizationsError.isLeft())
        organizationsError.mapLeft {
            assertEquals(
                    "Failed to parse Organizations in organizations_invalid.json: " +
                            "Required value 'id' (JSON name '_id') missing at \$[1]",
                    it.message)
        }
    }

    // User missing _id field
    @Test fun testLoadingInvalidUsers() {
        val usersError = loadFile("User", "users_invalid")
        assertTrue(usersError.isLeft())
        usersError.mapLeft {
            assertNotNull(it.message)
            assertTrue(it.message!!.contains("Failed to parse Users in users_invalid.json:"))
        }
    }

    // Ticket missing _id field
    @Test fun testLoadingInvalidTickets() {
        val ticketsError = loadFile("Ticket", "tickets_invalid")
        assertTrue(ticketsError.isLeft())
        ticketsError.mapLeft {
            assertNotNull(it.message)
            assertTrue(it.message!!.contains("Failed to parse Tickets in tickets_invalid.json:"))
        }
    }

    // Invalid entity class name
    @Test fun testInvalidEntityType() {
        val invalidEntityError = loadFile("Invalid", "users")
        assert(invalidEntityError.isLeft())
        invalidEntityError.mapLeft {
            assertNotNull(it.message)
            assertTrue(it.message!!.contains("Entity class Invalid does not exist:"))
        }
    }

    // Invalid file name
    @Test fun testMissingFile() {
        val missingFileError = loadFile("Organization", "invalid_filename")
        assertTrue(missingFileError.isLeft())
        missingFileError.mapLeft {
            assertNotNull(it.message)
            assertTrue(it.message!!.contains("Invalid file name invalid_filename.json:"))
        }
    }
}