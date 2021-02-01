package search.cli

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IndexesTest {

    // Happy cases

    @Test fun testCreatingIndexes() {
        val organizations = createMockOrganizations()
        val users = createMockUsers()
        val tickets = createMockTickets()

        val indexes = createIndexes(listOf(organizations, users, tickets))

        assertTrue(indexes.isRight())
        indexes.map {
            assertTrue(it.isNotEmpty())

            // Test Organization index map
            assertNotNull(it[0])
            assertTrue(it[0].containsKey("id"))
            assertNotNull(it[0]["id"])
            assertTrue(it[0]["id"]!!.containsKey("001"))
            assertTrue(it[0]["id"]!!["001"]!!.contains("001"))

            assertTrue(it[0].containsKey("name"))
            assertNotNull(it[0]["name"])
            assertTrue(it[0]["name"]!!.containsKey("AliceCorp"))
            assertTrue(it[0]["name"]!!["AliceCorp"]!!.contains("001"))

            // Test User index map
            assertNotNull(it[1])
            assertTrue(it[1].containsKey("id"))
            assertNotNull(it[1]["id"])
            assertTrue(it[1]["id"]!!.containsKey("0001"))
            assertTrue(it[1]["id"]!!["0001"]!!.contains("0001"))

            assertTrue(it[1].containsKey("name"))
            assertNotNull(it[1]["name"])
            assertTrue(it[1]["name"]!!.containsKey("Alice"))
            assertTrue(it[1]["name"]!!["Alice"]!!.contains("0001"))

            // Test Ticket index map
            assertNotNull(it[2])
            assertTrue(it[2].containsKey("id"))
            assertNotNull(it[2]["id"])
            assertTrue(it[2]["id"]!!.containsKey("00001"))
            assertTrue(it[2]["id"]!!["00001"]!!.contains("00001"))

            assertTrue(it[2].containsKey("subject"))
            assertNotNull(it[2]["subject"])
            assertTrue(it[2]["subject"]!!.containsKey("AliceIssue"))
            assertTrue(it[2]["subject"]!!["AliceIssue"]!!.contains("00001"))
            assertTrue(it[2]["subject"]!!.containsKey("BobIssue"))
            assertTrue(it[2]["subject"]!!["BobIssue"]!!.contains("00002"))
        }
    }

    @Test fun testCreatingOrganizationIndex() {

        val organizations = createMockOrganizations()
        val organizationIndex = createIndex("Organization", organizations)

        assertTrue(organizationIndex.isRight())
        organizationIndex.map {
            assertTrue(it.containsKey("id"))
            assertNotNull(it["id"])
            assertTrue(it["id"]!!.containsKey("001"))
            assertTrue(it["id"]!!["001"]!!.contains("001"))

            assertTrue(it.containsKey("name"))
            assertNotNull(it["name"])
            assertTrue(it["name"]!!.containsKey("AliceCorp"))
            assertTrue(it["name"]!!["AliceCorp"]!!.contains("001"))

            assertTrue(it.containsKey("tags"))
            assertNotNull(it["tags"])
            assertTrue(it["tags"]!!.containsKey("Fulton"))
            assertTrue(it["tags"]!!["Fulton"]!!.contains("001"))
            assertTrue(it["tags"]!!["Fulton"]!!.contains("002"))

            assertTrue(it.containsKey("details"))
            assertNotNull(it["details"])
            assertTrue(it["details"]!!.containsKey("NULL_OR_EMPTY"))
            assertTrue(it["details"]!!["NULL_OR_EMPTY"]!!.contains("001"))
            assertTrue(it["details"]!!["NULL_OR_EMPTY"]!!.contains("002"))

            assertTrue(it.containsKey("externalId"))
            assertNotNull(it["externalId"])
            assertTrue(it["externalId"]!!.containsKey("NULL_OR_EMPTY"))
            assertTrue(it["externalId"]!!["NULL_OR_EMPTY"]!!.contains("001"))
            assertTrue(it["externalId"]!!["NULL_OR_EMPTY"]!!.contains("002"))
        }
    }

    @Test fun testCreatingUserIndex() {

        val users = createMockUsers()
        val userIndex = createIndex("User", users)

        assertTrue(userIndex.isRight())
        userIndex.map {
            assertTrue(it.containsKey("id"))
            assertNotNull(it["id"])
            assertTrue(it["id"]!!.containsKey("0001"))
            assertTrue(it["id"]!!["0001"]!!.contains("0001"))

            assertTrue(it.containsKey("name"))
            assertNotNull(it["name"])
            assertTrue(it["name"]!!.containsKey("Alice"))
            assertTrue(it["name"]!!["Alice"]!!.contains("0001"))

            assertTrue(it.containsKey("tags"))
            assertNotNull(it["tags"])
            assertTrue(it["tags"]!!.containsKey("Fulton"))
            assertTrue(it["tags"]!!["Fulton"]!!.contains("0001"))
            assertTrue(it["tags"]!!["Fulton"]!!.contains("0002"))

            assertTrue(it.containsKey("alias"))
            assertNotNull(it["alias"])
            assertTrue(it["alias"]!!.containsKey("NULL_OR_EMPTY"))
            assertTrue(it["alias"]!!["NULL_OR_EMPTY"]!!.contains("0001"))
            assertTrue(it["alias"]!!["NULL_OR_EMPTY"]!!.contains("0002"))

            assertTrue(it.containsKey("signature"))
            assertNotNull(it["signature"])
            assertTrue(it["signature"]!!.containsKey("NULL_OR_EMPTY"))
            assertTrue(it["signature"]!!["NULL_OR_EMPTY"]!!.contains("0001"))
            assertTrue(it["signature"]!!["NULL_OR_EMPTY"]!!.contains("0002"))
        }
    }

    @Test fun testCreatingTicketIndex() {

        val tickets = createMockTickets()
        val ticketIndex = createIndex("Ticket", tickets)

        assertTrue(ticketIndex.isRight())

        ticketIndex.map {
            assertTrue(it.containsKey("id"))
            assertNotNull(it["id"])
            assertTrue(it["id"]!!.containsKey("00001"))
            assertTrue(it["id"]!!["00001"]!!.contains("00001"))

            assertTrue(it.containsKey("subject"))
            assertNotNull(it["subject"])
            assertTrue(it["subject"]!!.containsKey("AliceIssue"))
            assertTrue(it["subject"]!!["AliceIssue"]!!.contains("00001"))
            assertTrue(it["subject"]!!.containsKey("BobIssue"))
            assertTrue(it["subject"]!!["BobIssue"]!!.contains("00002"))

            assertTrue(it.containsKey("tags"))
            assertNotNull(it["tags"])
            assertTrue(it["tags"]!!.containsKey("American Samoa"))
            assertTrue(it["tags"]!!["American Samoa"]!!.contains("00001"))
            assertTrue(it["tags"]!!["American Samoa"]!!.contains("00002"))

            assertTrue(it.containsKey("via"))
            assertNotNull(it["via"])
            assertTrue(it["via"]!!.containsKey("NULL_OR_EMPTY"))
            assertTrue(it["via"]!!["NULL_OR_EMPTY"]!!.contains("00001"))
            assertTrue(it["via"]!!["NULL_OR_EMPTY"]!!.contains("00002"))

            assertTrue(it.containsKey("description"))
            assertNotNull(it["description"])
            assertTrue(it["description"]!!.containsKey("NULL_OR_EMPTY"))
            assertTrue(it["description"]!!["NULL_OR_EMPTY"]!!.contains("00001"))
            assertTrue(it["description"]!!["NULL_OR_EMPTY"]!!.contains("00002"))
        }
    }

    // Unhappy cases

    // Incorrect list of entity maps provided
    @Test
    fun testCreatingIndexesWithInvalidEntityMap() {
        val organizations = createMockOrganizations()
        val users = createMockUsers()

        val indexesError = createIndexes(listOf(organizations, users, users))
        assertTrue(indexesError.isLeft())
        indexesError.mapLeft {
            assertNotNull(it.message)
            assertTrue(it.message!!.contains("Ticket does not match the entity type in the provided map:"))
        }
    }

    @Test
    fun testCreateIndexWithInvalidEntityClassname() {
        val organizations = createMockOrganizations()

        val indexError = createIndex("Invalid", organizations)
        assertTrue(indexError.isLeft())

        indexError.mapLeft {
            assertNotNull(it.message)
            assertTrue(it.message!!.contains("Entity class Invalid does not exist:"))
        }
    }
}