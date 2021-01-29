package search.cli

import com.github.ajalt.clikt.output.TermUi
import kotlin.reflect.full.memberProperties

class PromptOptions {
    companion object {

        // TODO: make this an enum
        val ENTITY_TYPES = listOf("User", "Ticket", "Organization")

        val INTRODUCTION = "Welcome to Zendesk Search!\n" +
                "Type 'ctrl+c' at any time to exit\n"

        val INTRO_SELECTION = "       Select search option:\n" +
                "       * Press 1 to search Zendesk\n" +
                "       * Press 2 to view searchable fields\n"

        // TODO: derive this string from ENTITY_TYPES
        val ENTITY_SELECTION = "Select 1) Users, 2) Organizations, 3) Tickets"

        val ENTITY_SEARCH_OPTIONS = "Enter search term or hit enter to view fields for "
        
    }
}

fun promptStateMachineInit() {
    val initialOption = getIntroSelection()
    val entitySelection = getEntitySelection(initialOption)
    handleSearchOptions(entitySelection)
}

fun handleSearchOptions(entitySelection: Int) {
    val searchTerm = getSearchTerm(entitySelection)
    val searchValue = getSearchValue(searchTerm, entitySelection) 
    val resetSelection = getResults(searchValue, searchTerm, entitySelection)
    if (resetSelection == 1) promptStateMachineInit() else handleSearchOptions(entitySelection)
}

fun getIntroSelection(init: Boolean = true): Int {
    val promptText = if (init) PromptOptions.INTRODUCTION else ""
    val initialSelection = TermUi.prompt(promptText + PromptOptions.INTRO_SELECTION)

    return if (initialSelection == null || initialSelection.toIntOrNull() == null ) {
        getIntroSelection(false)
    } else {
        initialSelection.toInt()
    }
}

fun getEntitySelection(introSelection: Int): Int {
    val promptText = if (introSelection == 2) {
        PromptOptions.ENTITY_TYPES.map { printSearchableFields(it) }
                .reduce {acc, it -> acc + it} + "\n" + PromptOptions.ENTITY_SELECTION
    } else {
        PromptOptions.ENTITY_SELECTION
    }
    val entitySelection = TermUi.prompt(promptText)

    return if (entitySelection == null || entitySelection.toIntOrNull() == null) {
        getEntitySelection(1)
    } else {
        entitySelection.toInt()
    }
}

fun getSearchTerm(entityType: Int): String {
    val searchTerm = TermUi.prompt(PromptOptions.ENTITY_SEARCH_OPTIONS + entityType)
    return if (searchTerm == null || !isValidSearchTerm(searchTerm, entityType)) {
        // TODO: display fields for entityType
        getSearchTerm(entityType)
    } else {
        searchTerm
    }
}

fun getSearchValue(searchTerm: String, entitySelection: Int): String = "TODO"

fun getResults(searchValue: String, searchTerm: String, entitySelection: Int): Int = 1

/* In order to make this application extensible, the property names that can be
searched on should not be hard coded.
Here, reflection is used to analyse the properties of the classes to create the
list of searchable fields without having to hardcode any of them.
 */
fun printSearchableFields(entityType: String): String {
    val header = "--------------------------------------\n" +
            "Search ${entityType}s with\n"

    val entityProperties = Class.forName("search.cli.$entityType").kotlin.memberProperties
            .map { it.name }
            .reduce {acc, it -> acc + "\n" + it}

    return "$header $entityProperties \n"
}

fun isValidSearchTerm(searchTerm: String, entityType: Int): Boolean = true