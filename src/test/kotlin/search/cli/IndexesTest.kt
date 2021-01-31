package search.cli

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IndexesTest {
    @Test fun testCreatingOrganizationIndex() {

        val organizations = createMockOrganizations()
        val organizationIndex = createIndex("Organization", organizations)

        assertTrue(organizationIndex.isRight())
        organizationIndex.map {
            assertTrue(it.containsKey("_id"))
            assertNotNull(it["_id"])
            assertTrue(it["_id"]!!.containsKey("001"))
            assertTrue(it["_id"]!!["001"]!!.contains("001"))

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

            assertTrue(it.containsKey("external_id"))
            assertNotNull(it["external_id"])
            assertTrue(it["external_id"]!!.containsKey("NULL_OR_EMPTY"))
            assertTrue(it["external_id"]!!["NULL_OR_EMPTY"]!!.contains("001"))
            assertTrue(it["external_id"]!!["NULL_OR_EMPTY"]!!.contains("002"))
        }
    }

    @Test fun testCreatingUserIndex() {

        val users = createMockUsers()
        val userIndex = createIndex("User", users)

        assertTrue(userIndex.isRight())
        userIndex.map {
            assertTrue(it.containsKey("_id"))
            assertNotNull(it["_id"])
            assertTrue(it["_id"]!!.containsKey("0001"))
            assertTrue(it["_id"]!!["0001"]!!.contains("0001"))

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
            assertTrue(it.containsKey("_id"))
            assertNotNull(it["_id"])
            assertTrue(it["_id"]!!.containsKey("00001"))
            assertTrue(it["_id"]!!["00001"]!!.contains("00001"))

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
}