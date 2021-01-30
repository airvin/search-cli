package search.cli

import com.github.ajalt.clikt.output.TermUi
import kotlin.reflect.full.memberProperties

fun promptStateMachineInit() {
    val introSelection = getIntroSelection()
    val entitySelection = getEntitySelection(introSelection == 2)
    handleSearchOptions(entitySelection)
}

fun handleSearchOptions(entitySelection: String) {
    val searchTerm = getSearchTerm(entitySelection)
    val searchValue = getSearchValue(searchTerm, entitySelection) 
    val results = getResults(searchValue, searchTerm, entitySelection)
    println(results)
    val promptResetSelection = getResetSelection(entitySelection)
    if (promptResetSelection == 1) handleSearchOptions(entitySelection) else promptStateMachineInit()
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
    val entitySelectionText = "Select entity to search \n${Entity.values.mapIndexed {i, it -> "${i+1}) $it\n"}
            .reduce { acc, it -> acc + it }}"

    val entityFields = if (displayFields) {
        Entity.values.map { printSearchableFields(it) }
                .reduce {acc, it -> acc + it} + "\n"
    } else ""

    val entitySelection = TermUi.prompt(entityFields + entitySelectionText)

    return if (entitySelection == null
            || entitySelection.toIntOrNull() == null
            || entitySelection.toInt() > Entity.values().size
            || Entity.getByInt(entitySelection.toInt()) == null
    ) {
        getEntitySelection(false)
    } else {
        Entity.getByInt(entitySelection.toInt())!!.toString().toLowerCase().capitalize()
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