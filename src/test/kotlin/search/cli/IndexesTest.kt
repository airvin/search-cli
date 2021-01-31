package search.cli

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IndexesTest {
    @Test fun testCreatingOrganizationIndex() {

        val organizations = createMockOrganizations()
        val organizationIndex = createIndex("Organization", organizations)

        assertTrue(!organizationIndex.isEmpty())

        assertTrue(organizationIndex.containsKey("_id"))
        assertNotNull(organizationIndex["_id"])
        assertTrue(organizationIndex["_id"]!!.containsKey("001"))
        assertTrue(organizationIndex["_id"]!!["001"]!!.contains("001"))

        assertTrue(organizationIndex.containsKey("name"))
        assertNotNull(organizationIndex["name"])
        assertTrue(organizationIndex["name"]!!.containsKey("AliceCorp"))
        assertTrue(organizationIndex["name"]!!["AliceCorp"]!!.contains("001"))

        // TODO: Fix createIndex to flatten lists of strings (i.e. for tags and domain names)
        assertTrue(organizationIndex.containsKey("tags"))
        assertNotNull(organizationIndex["tags"])
//        assertTrue(organizationIndex["tags"]!!.containsKey("Fulton"))
//        assertTrue(organizationIndex["tags"]!!["Fulton"]!!.contains("001"))
//        assertTrue(organizationIndex["tags"]!!["Fulton"]!!.contains("002"))

        // TODO: Fix createIndex to store null fields
        assertTrue(organizationIndex.containsKey("details"))
        assertNotNull(organizationIndex["details"])
//        assertTrue(organizationIndex["details"]!!.containsKey(""))
//        assertTrue(organizationIndex["details"]!![""]!!.contains("001"))
//        assertTrue(organizationIndex["details"]!![""]!!.contains("002"))

        // TODO: Fix createIndex to store fields with empty strings
        assertTrue(organizationIndex.containsKey("external_id"))
        assertNotNull(organizationIndex["external_id"])
//        assertTrue(organizationIndex["external_id"]!!.containsKey(""))
//        assertTrue(organizationIndex["external_id"]!![""]!!.contains("001"))
//        assertTrue(organizationIndex["external_id"]!![""]!!.contains("002"))
    }

    @Test fun testCreatingUserIndex() {

        val users = createMockUsers()
        val userIndex = createIndex("User", users)

        assertTrue(!userIndex.isEmpty())

        assertTrue(userIndex.containsKey("_id"))
        assertNotNull(userIndex["_id"])
        assertTrue(userIndex["_id"]!!.containsKey("0001"))
        assertTrue(userIndex["_id"]!!["0001"]!!.contains("0001"))

        assertTrue(userIndex.containsKey("name"))
        assertNotNull(userIndex["name"])
        assertTrue(userIndex["name"]!!.containsKey("Alice"))
        assertTrue(userIndex["name"]!!["Alice"]!!.contains("0001"))

        // TODO: Fix createIndex to flatten lists of strings (i.e. for tags and domain names)
        assertTrue(userIndex.containsKey("tags"))
        assertNotNull(userIndex["tags"])
//        assertTrue(userIndex["tags"]!!.containsKey("Fulton"))
//        assertTrue(userIndex["tags"]!!["Fulton"]!!.contains("0001"))
//        assertTrue(userIndex["tags"]!!["Fulton"]!!.contains("0002"))

        // TODO: Fix createIndex to store null fields
        assertTrue(userIndex.containsKey("alias"))
        assertNotNull(userIndex["alias"])
//        assertTrue(userIndex["alias"]!!.containsKey(""))
//        assertTrue(userIndex["alias"]!![""]!!.contains("0001"))
//        assertTrue(userIndex["alias"]!![""]!!.contains("0002"))

        // TODO: Fix createIndex to store fields with empty strings
        assertTrue(userIndex.containsKey("signature"))
        assertNotNull(userIndex["signature"])
//        assertTrue(userIndex["signature"]!!.containsKey(""))
//        assertTrue(userIndex["signature"]!![""]!!.contains("0001"))
//        assertTrue(userIndex["signature"]!![""]!!.contains("0002"))
    }

    @Test fun testCreatingTicketIndex() {

        val tickets = createMockTickets()
        val ticketIndex = createIndex("Ticket", tickets)

        assertTrue(!ticketIndex.isEmpty())

        assertTrue(ticketIndex.containsKey("_id"))
        assertNotNull(ticketIndex["_id"])
        assertTrue(ticketIndex["_id"]!!.containsKey("00001"))
        assertTrue(ticketIndex["_id"]!!["00001"]!!.contains("00001"))

        assertTrue(ticketIndex.containsKey("subject"))
        assertNotNull(ticketIndex["subject"])
        assertTrue(ticketIndex["subject"]!!.containsKey("AliceIssue"))
        assertTrue(ticketIndex["subject"]!!["AliceIssue"]!!.contains("00001"))
        assertTrue(ticketIndex["subject"]!!.containsKey("BobIssue"))
        assertTrue(ticketIndex["subject"]!!["BobIssue"]!!.contains("00002"))

        // TODO: Fix createIndex to flatten lists of strings (i.e. for tags and domain names)
        assertTrue(ticketIndex.containsKey("tags"))
        assertNotNull(ticketIndex["tags"])
//        assertTrue(ticketIndex["tags"]!!.containsKey("American Samoa"))
//        assertTrue(ticketIndex["tags"]!!["American Samoa"]!!.contains("00001"))
//        assertTrue(ticketIndex["tags"]!!["American Samoa"]!!.contains("00002"))

        // TODO: Fix createIndex to store null fields
        assertTrue(ticketIndex.containsKey("via"))
        assertNotNull(ticketIndex["via"])
//        assertTrue(ticketIndex["via"]!!.containsKey(""))
//        assertTrue(ticketIndex["via"]!![""]!!.contains("00001"))
//        assertTrue(ticketIndex["via"]!![""]!!.contains("00002"))

        // TODO: Fix createIndex to store fields with empty strings
        assertTrue(ticketIndex.containsKey("description"))
        assertNotNull(ticketIndex["description"])
//        assertTrue(ticketIndex["description"]!!.containsKey(""))
//        assertTrue(ticketIndex["description"]!![""]!!.contains("00001"))
//        assertTrue(ticketIndex["description"]!![""]!!.contains("00002"))
    }
}