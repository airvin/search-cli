package search.cli

import com.github.ajalt.clikt.output.TermUi
import kotlin.reflect.full.memberProperties

class PromptOptions {
    companion object {

        val INTRODUCTION = "Welcome to Zendesk Search!\n" +
                "Type 'ctrl+c' at any time to exit\n"

        val INTRO_SELECTION = "       Select search option:\n" +
                "       * Press 1 to search Zendesk\n" +
                "       * Press 2 to view searchable fields\n"

        val ENTITY_SELECTION = "Select entity to search \n${Entity.values.mapIndexed {i, it -> "${i+1}) $it\n"}
                .reduce { acc, it -> acc + it }}"

        val ENTITY_SEARCH_OPTIONS = "Enter search term or hit enter to view fields for "

    }
}


fun promptStateMachineInit() {
    val introSelection = getIntroSelection()
    val entitySelection = getEntitySelection(introSelection == 2)
    handleSearchOptions(entitySelection)
}

fun handleSearchOptions(entitySelection: Entity) {
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

fun getEntitySelection(displayFields: Boolean): Entity {
    val promptText = if (displayFields) {
        Entity.values.map { printSearchableFields(it) }
                .reduce {acc, it -> acc + it} + "\n" + PromptOptions.ENTITY_SELECTION
    } else {
        PromptOptions.ENTITY_SELECTION
    }
    val entitySelection = TermUi.prompt(promptText)

    return if (entitySelection == null
            || entitySelection.toIntOrNull() == null
            || entitySelection.toInt() > Entity.values().size
            || Entity.getByInt(entitySelection.toInt()) == null
    ) {
        getEntitySelection(false)
    } else {
        Entity.getByInt(entitySelection.toInt())!!
    }
}

fun getSearchTerm(entityType: Entity): String {
    val searchTerm = TermUi.prompt(PromptOptions.ENTITY_SEARCH_OPTIONS + entityType.toString().toLowerCase().capitalize())
    return if (searchTerm == null || !isValidSearchTerm(searchTerm, entityType)) {
        // TODO: display fields for entityType
        getSearchTerm(entityType)
    } else {
        searchTerm
    }
}

fun getSearchValue(searchTerm: String, entitySelection: Entity): String = "TODO"

fun getResults(searchValue: String, searchTerm: String, entitySelection: Entity): Int = 1

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

fun isValidSearchTerm(searchTerm: String, entityType: Entity): Boolean = true