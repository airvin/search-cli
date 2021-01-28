package search.cli

data class Organization(
        val _id: String,
        val url: String,
        val external_id: String,
        val created_at: String,
        val tags: List<String>,
        val name: String,
        val domain_names: List<String>,
        val details: String,
        val shared_tickets: Boolean
)

data class Ticket(
        val _id: String,
        val url: String,
        val external_id: String,
        val created_at: String,
        val tags: List<String>,
        val type: String,
        val subject: String,
        val description: String,
        val priority: String,
        val status: String,
        val submitter_id: String,
        val assignee_id: String,
        val organization_id: String,
        val has_incidents: Boolean,
        val due_at: String,
        val via: String,
        val requester_id: String
)

data class User(
        val _id: String,
        val url: String,
        val external_id: String,
        val created_at: String,
        val tags: List<String>,
        val name: String,
        val alias: String,
        val active: Boolean,
        val verified: Boolean,
        val shared: Boolean,
        val locale: String,
        val timezone: String,
        val last_login_at: String,
        val email: String,
        val phone: String,
        val signature: String,
        val organization_id: String,
        val suspended: Boolean,
        val role: String
)

