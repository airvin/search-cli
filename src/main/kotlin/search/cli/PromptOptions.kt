package search.cli

import kotlin.reflect.full.memberProperties

class PromptOptions {
    companion object {
        val ENTITY_TYPES = listOf("User", "Ticket", "Organization")

        val INTRODUCTION = "Welcome to Zendesk Search!\n" +
                "Type 'ctrl+c' at any time to exit\n" +
                "       Select search option:\n" +
                "       * Press 1 to search Zendesk\n" +
                "       * Press 2 to view searchable fields\n"

        val INTRO_ERROR = "       Select search option:\n" +
                "       * Press 1 to search Zendesk\n" +
                "       * Press 2 to view searchable fields\n"

        val ENTITY_SELECTION = "Select 1) Users, 2) Organizations, 3) Tickets"

        val ENTER_OR_VIEW_FIELDS = "Enter search term or hit enter to view fields for "
    }
}


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

