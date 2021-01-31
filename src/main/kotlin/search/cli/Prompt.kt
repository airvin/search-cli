package search.cli

import com.github.ajalt.clikt.output.TermUi

class Prompt(
        val organizations: Map<String, Organization>,
        val users: Map<String, User>,
        val tickets: Map<String, Ticket>,
        val organizationIndex: Map<String, MutableMap<String, MutableList<String>>>,
        val userIndex: Map<String, MutableMap<String, MutableList<String>>>,
        val ticketIndex: Map<String, MutableMap<String, MutableList<String>>>
) {

    fun init() {
        val introSelection = getIntroSelection()
        val entitySelection = getEntitySelection(introSelection == 2)
        handleSearchOptions(entitySelection)
    }

    fun handleSearchOptions(entity: EntityEnum) {
        val searchTerm = getSearchTerm(entity.toString().toLowerCase().capitalize())
        val searchValue = getSearchValue(searchTerm, entity.toString().toLowerCase().capitalize())
        val matchingEntities: List<Entity> = when(entity) {
            EntityEnum.ORGANIZATION -> search(searchTerm, searchValue, organizationIndex, organizations)
            EntityEnum.USER -> search(searchTerm, searchValue, userIndex, users)
            EntityEnum.TICKET -> search(searchTerm, searchValue, ticketIndex, tickets)
        }
        when(entity) {
            EntityEnum.ORGANIZATION -> {
                val relatedEntities = findEntitiesRelatedToOrgs(userIndex, ticketIndex, matchingEntities as List<Organization>)
                prettyPrintResults(matchingEntities, relatedEntities, EntityEnum.ORGANIZATION, users, tickets, searchTerm, searchValue)
            }
            EntityEnum.USER -> {
                val relatedEntities = findEntitiesRelatedToUser(organizationIndex, ticketIndex, matchingEntities as List<User>)
                prettyPrintResults(matchingEntities, relatedEntities, EntityEnum.USER, organizations, tickets, searchTerm, searchValue)
            }
            EntityEnum.TICKET -> {
                val relatedEntities = findEntitiesRelatedToTicket(userIndex, organizationIndex, matchingEntities as List<Ticket>)
                prettyPrintResults(matchingEntities, relatedEntities, EntityEnum.TICKET, organizations, users, searchTerm, searchValue)
            }
        }

        val resetSelection = getResetSelection(entity.toString().toLowerCase().capitalize())
        if (resetSelection == 1) handleSearchOptions(entity) else init()
    }
}


fun getIntroSelection(init: Boolean = true): Int {
    val introduction = "\nWelcome to Zendesk Search!\n" +
            "Type 'ctrl+c' at any time to exit\n"

    val intro_selection = "\n       Select search option:\n" +
            "       * Press 1 to search Zendesk\n" +
            "       * Press 2 to view searchable fields\n\n"

    val introText = if (init) introduction else ""
    val initialSelection = TermUi.prompt(introText + intro_selection)

    return if (initialSelection == null || initialSelection.toIntOrNull() == null ) {
        getIntroSelection(false)
    } else {
        initialSelection.toInt()
    }
}


fun getEntitySelection(displayFields: Boolean): EntityEnum {
    // Get the list of entity types from the EntityEnum class and print as a numbered list
    val entitySelectionText = "\nSelect entity to search \n${EntityEnum.values.mapIndexed {i, it -> "${i+1}) $it\n"}
            .reduce { acc, it -> acc + it }}"

    // Get the searchable fields for each entity if the user has chosen to see the field options
    val entityFields = if (displayFields) {
        EntityEnum.values.map { printSearchableFields(it) }
                .reduce {acc, it -> acc + it} + "\n"
    } else ""

    val entitySelection = TermUi.prompt(entityFields + entitySelectionText)

    return if (entitySelection == null
            || entitySelection.toIntOrNull() == null
            || entitySelection.toInt() > EntityEnum.values().size
            || EntityEnum.getByInt(entitySelection.toInt()) == null
    ) {
        getEntitySelection(false)
    } else {
        EntityEnum.getByInt(entitySelection.toInt())!!
    }
}


fun getSearchTerm(entity: String, displayFields: Boolean = false): String {
    val entityFields = if (displayFields) printSearchableFields(entity) else ""
    val searchTerm = TermUi.prompt("${entityFields}\nEnter search term for $entity", "")
    return if (searchTerm.isNullOrEmpty() || !isValidSearchTerm(searchTerm, entity)) {
        getSearchTerm(entity, true)
    } else {
        searchTerm
    }
}


fun getSearchValue(searchTerm: String, entity: String): String {
    val searchValue = TermUi.prompt("\nEnter search value for $entity $searchTerm", default = "NULL_OR_EMPTY", showDefault = false)
    return if (searchValue.isNullOrEmpty()) getSearchValue(searchTerm, entity) else searchValue
}


fun getResetSelection(entity: String): Int {
    val resetSelection = TermUi.prompt("Enter 1 to search $entity again, or 2 to restart from the start")
    return if (resetSelection == null || resetSelection.toIntOrNull() == null) {
        getResetSelection(entity)
    } else {
        resetSelection.toInt()
    }
}
