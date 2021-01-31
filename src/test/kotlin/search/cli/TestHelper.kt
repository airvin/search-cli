package search.cli

fun createMockOrganization(id: String = "001", name: String = "abc"): Organization = Organization(id,
        "http://$name.com/api/v2/organizations/101.json",
        "",
        "2016-05-21T11:10:28 -10:00",
        listOf("Fulton", "West", "Rodriguez", "Farley"),
        name,
        listOf("$name.com", "$name.com.au", "$name.org"),
        null,
        false
)

fun createMockOrganizations(): Map<String, Organization> {
    return mapOf("001" to createMockOrganization("001", "AliceCorp"),
    "002" to createMockOrganization("002", "BobCorp"))
}

fun createMockUser(id: String = "0001", name: String = "Alice", orgId: String = "001"): User = User(
        id,
        "http://$name.com/api/v2/users/101.json",
        "74341f74-9c79-49d5-9611-87ef9b6eb75f",
        "2016-04-15T05:19:46 -10:00",
        listOf("Fulton", "West", "Rodriguez", "Farley"),
        name,
        null,
        true,
        true,
        false,
        "en-AU",
        "AEST",
        "2013-08-04T01:03:27 -10:00",
        "$name@example.com",
        "8335-422-718",
        "",
        orgId,
        false,
        "admin"
)

fun createMockUsers(): Map<String, User> {
    return mapOf("0001" to createMockUser("0001", "Alice", "001"),
            "0002" to createMockUser("0002", "Bob", "002"))
}

fun createMockTicket(
        id: String = "00001",
        subject: String = "AliceIssue",
        orgId: String = "001",
        submitterId: String = "0001",
        assigneeId: String = "0001",
        requesterId: String? = null
): Ticket = Ticket(
        id,
        "http://initech.zendesk.com/api/v2/tickets/101.json",
        "9210cdc9-4bee-485f-a078-35396cd74063",
        "2016-04-15T05:19:46 -10:00",
        listOf("Ohio", "Pennsylvania", "American Samoa", "Northern Mariana Islands"),
        "incident",
        subject,
        "",
        "high",
        "pending",
        submitterId,
        assigneeId,
        orgId,
        false,
        "2016-07-31T02:37:50 -10:00",
        null,
        requesterId
)

fun createMockTickets(): Map<String, Ticket> {
    return mapOf("00001" to createMockTicket("00001", "AliceIssue", "001", "0001", "0001"),
            "0002" to createMockTicket("00002", "BobIssue", "001", "0002", "0001", "0002"))
}