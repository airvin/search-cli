package search.cli

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SearchTest {
    @kotlin.test.Test fun testSearchOrganization() {

        val organizations = createMockOrganizations()
        val organizationIndex = createIndex("Organization", organizations)
        val idSearchResult = search("_id", "001", organizationIndex, organizations) as List<Organization>

        assertNotNull(idSearchResult)
        assertTrue(idSearchResult.isNotEmpty())
        assertTrue(idSearchResult.contains(organizations["001"]))
        assertTrue(!idSearchResult.contains(organizations["002"]))

        val idMissingSearchResult = search("_id", "111", organizationIndex, organizations) as List<Organization>
        assertNotNull(idMissingSearchResult)
        assertTrue(idMissingSearchResult.isEmpty())
    }
}