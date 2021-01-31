package search.cli


enum class EntityEnum {
    ORGANIZATION, USER, TICKET;

    companion object {
        val values = values().map { it.toString().toLowerCase().capitalize() }

        // TODO: import arrow to use the either type for error handling here
        fun getByInt(i: Int) = values().getOrNull(i - 1)
    }
}

sealed class Entity(
        open val _id: String,
        open val url: String?,
        open val external_id: String?,
        open val created_at: String?) {
}

data class Organization(
        override val _id: String,
        override val url: String?,
        override val external_id: String?,
        override val created_at: String?,
        val tags: List<String>?,
        val name: String?,
        val domain_names: List<String>?,
        val details: String?,
        val shared_tickets: Boolean?
) : Entity(_id, url, external_id, created_at)

data class Ticket(
        override val _id: String,
        override val url: String?,
        override val external_id: String?,
        override val created_at: String?,
        val tags: List<String>?,
        val type: String?,
        val subject: String?,
        val description: String?,
        val priority: String?,
        val status: String?,
        val submitter_id: String?,
        val assignee_id: String?,
        val organization_id: String?,
        val has_incidents: Boolean?,
        val due_at: String?,
        val via: String?,
        val requester_id: String?
) : Entity(_id, url, external_id, created_at)

data class User(
        override val _id: String,
        override val url: String?,
        override val external_id: String?,
        override val created_at: String?,
        val tags: List<String>?,
        val name: String?,
        val alias: String?,
        val active: Boolean?,
        val verified: Boolean?,
        val shared: Boolean?,
        val locale: String?,
        val timezone: String?,
        val last_login_at: String?,
        val email: String?,
        val phone: String?,
        val signature: String?,
        val organization_id: String?,
        val suspended: Boolean?,
        val role: String?
) : Entity(_id, url, external_id, created_at)

