package search.cli

import com.github.ajalt.clikt.output.TermUi
import kotlin.reflect.full.memberProperties

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

    fun handleSearchOptions(entity: String) {
        val searchTerm = getSearchTerm(entity)
        val searchValue = getSearchValue(searchTerm, entity)
        val matchingEntities: List<Entity> = when(entity) {
            "Organization" -> search(searchTerm, searchValue, organizationIndex, organizations)
            "User" -> search(searchTerm, searchValue, userIndex, users)
            "Ticket" -> search(searchTerm, searchValue, ticketIndex, tickets)
            else -> listOf()
        }
        val relatedEntities: List<Entity> = when(entity) {
            "Organization" -> matchingEntities.flatMap {
                findEntitiesRelatedToOrg(userIndex, users, ticketIndex, tickets, it as Organization) }
            "User" -> matchingEntities.flatMap {
                findEntitiesRelatedToUser(userIndex, users, ticketIndex, tickets, it as User) }
            "Ticket" -> matchingEntities.flatMap {
                findEntitiesRelatedToTicket(userIndex, users, organizationIndex, organizations, it as Ticket)}
            else -> listOf()
        }
        val results = (matchingEntities + relatedEntities).joinToString()

        println(results)
        val resetSelection = getResetSelection(entity)
        if (resetSelection == 1) handleSearchOptions(entity) else init()
    }
}


fun getIntroSelection(init: Boolean = true): Int {
    val introduction = "Welcome to Zendesk Search!\n" +
            "Type 'ctrl+c' at any time to exit\n"

    val intro_selection = "       Select search option:\n" +
            "       * Press 1 to search Zendesk\n" +
            "       * Press 2 to view searchable fields\n"

    val introText = if (init) introduction else ""
    val initialSelection = TermUi.prompt(introText + intro_selection)

    return if (initialSelection == null || initialSelection.toIntOrNull() == null ) {
        getIntroSelection(false)
    } else {
        initialSelection.toInt()
    }
}

fun getEntitySelection(displayFields: Boolean): String {
    val entitySelectionText = "Select entity to search \n${EntityEnum.values.mapIndexed {i, it -> "${i+1}) $it\n"}
            .reduce { acc, it -> acc + it }}"

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
        EntityEnum.getByInt(entitySelection.toInt())!!.toString().toLowerCase().capitalize()
    }
}

fun getSearchTerm(entity: String, displayFields: Boolean = false): String {
    val entityFields = if (displayFields) printSearchableFields(entity) else ""
    val searchTerm = TermUi.prompt("${entityFields}Enter search term for $entity", "")
    return if (searchTerm.isNullOrEmpty() || !isValidSearchTerm(searchTerm, entity)) {
        getSearchTerm(entity, true)
    } else {
        searchTerm
    }
}

fun getSearchValue(searchTerm: String, entity: String): String {
    val searchValue = TermUi.prompt("Enter search value for $entity $searchTerm")
    return if (searchValue.isNullOrEmpty()) getSearchValue(searchTerm, entity) else searchValue
}

fun getResults(searchValue: String, searchTerm: String, entity: String): String
    = "Results for searching $entity $searchTerm $searchValue"

fun getResetSelection(entity: String): Int {
    val resetSelection = TermUi.prompt("Enter 1 to search $entity again, or 2 to restart from the start")
    return if (resetSelection == null || resetSelection.toIntOrNull() == null) {
        getResetSelection(entity)
    } else {
        resetSelection.toInt()
    }
}

/* In order to make this application extensible, the property names that can be
searched on should not be hard coded.
Here, reflection is used to analyse the properties of the classes to create the
list of searchable fields without having to hardcode any of them.
 */
fun printSearchableFields(entity: String): String {
    val header = "--------------------------------------\n" +
            "Search ${entity}s with\n"

    val entityProperties = Class.forName("search.cli.$entity").kotlin.memberProperties
            .map { it.name }
            .reduce {acc, it -> acc + "\n" + it}

    return "$header $entityProperties \n"
}

fun isValidSearchTerm(searchTerm: String, entity: String): Boolean =
     Class.forName("search.cli.$entity").kotlin.memberProperties.map { it.name }.contains(searchTerm)

fun prettyPrintEntity(entity: Entity): String = "testing!"