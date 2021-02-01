package search.cli

import com.squareup.moshi.Json


enum class EntityEnum(val className: String) {
    ORGANIZATION("Organization"), USER("User"), TICKET("Ticket");

    companion object {
        val values = values().map { it.toString().toLowerCase().capitalize() }

        fun getByInt(i: Int) = values().getOrNull(i - 1)
    }
}

sealed class Entity(
        @Json(name = "_id") open val id: String,
        open val url: String?,
        @Json(name = "external_id") open val externalId: String?,
        @Json(name = "created_at") open val createdAt: String?) {
}

data class Organization(
        @Json(name = "_id") override val id: String,
        override val url: String?,
        @Json(name = "external_id") override val externalId: String?,
        @Json(name = "created_at") override val createdAt: String?,
        val tags: List<String>?,
        val name: String?,
        @Json(name = "domain_names") val domainNames: List<String>?,
        val details: String?,
        @Json(name = "shared_tickets") val sharedTickets: Boolean?
) : Entity(id, url, externalId, createdAt)

data class Ticket(
        @Json(name = "_id") override val id: String,
        override val url: String?,
        @Json(name = "external_id") override val externalId: String?,
        @Json(name = "created_at") override val createdAt: String?,
        val tags: List<String>?,
        val type: String?,
        val subject: String?,
        val description: String?,
        val priority: String?,
        val status: String?,
        @Json(name = "submitter_id") val submitterId: String?,
        @Json(name = "assignee_id") val assigneeId: String?,
        @Json(name = "organization_id") val organizationId: String?,
        @Json(name = "has_incidents") val hasIncidents: Boolean?,
        @Json(name = "due_at") val dueAt: String?,
        val via: String?,
        @Json(name = "requester_id") val requesterId: String?
) : Entity(id, url, externalId, createdAt)

data class User(
        @Json(name = "_id") override val id: String,
        override val url: String?,
        @Json(name = "external_id") override val externalId: String?,
        @Json(name = "created_at") override val createdAt: String?,
        val tags: List<String>?,
        val name: String?,
        val alias: String?,
        val active: Boolean?,
        val verified: Boolean?,
        val shared: Boolean?,
        val locale: String?,
        val timezone: String?,
        @Json(name = "last_login_at") val lastLoginAt: String?,
        val email: String?,
        val phone: String?,
        val signature: String?,
        @Json(name = "organization_id") val organizationId: String?,
        val suspended: Boolean?,
        val role: String?
) : Entity(id, url, externalId, createdAt)

