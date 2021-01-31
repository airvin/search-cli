package search.cli

import kotlin.reflect.full.memberProperties

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

fun prettyPrintResults(
        matchedEntities: List<Entity>,
        relatedEntities: List<Pair<MutableList<String>, MutableList<String>>>,
        entityType: EntityEnum,
        relatedEntitiesOne: Map<String, Entity>,
        relatedEntitiesTwo: Map<String, Entity>,
        searchTerm: String,
        searchValue: String
) {
    val ANSI_CYAN = "\u001B[36m"
    val ANSI_GREEN = "\u001B[32m";
    val ANSI_RESET = "\u001B[0m"
    val ANSI_BLUE = "\u001B[34m";
    val ANSI_PURPLE = "\u001B[35m";

    if (searchValue == "NULL_OR_EMPTY") {
        println("\n${ANSI_CYAN}Searching ${entityType.toString().toLowerCase().capitalize()} with an empty $searchTerm field returned ${matchedEntities.size} results $ANSI_RESET \n")
    } else {
        println("\n${ANSI_CYAN}Searching ${entityType.toString().toLowerCase().capitalize()} by $searchTerm $searchValue returned ${matchedEntities.size} results $ANSI_RESET \n")
    }
    if (matchedEntities.size > 0) {
        val (relatedTypeOne, relatedTypeTwo) = when (entityType) {
            EntityEnum.ORGANIZATION -> listOf("Users, Tickets")
            EntityEnum.USER -> listOf("Organizations", "Tickets")
            EntityEnum.TICKET -> listOf("Organizations", "Users")
        }
        matchedEntities.mapIndexed { i, it ->
            println("$ANSI_GREEN********************${entityType.toString().toLowerCase().capitalize()} ${i+1}********************$ANSI_RESET\n")
            println("$it \n")
            if (relatedEntities[i].first.size > 0 ) println("$ANSI_BLUE----------Related ${relatedTypeOne}----------$ANSI_RESET\n")
            relatedEntities[i].first.map { relatedEntitiesOne[it]!! }.map { println("$it \n") }
            if (relatedEntities[i].second.size > 0 ) println("$ANSI_PURPLE----------Related ${relatedTypeTwo}----------$ANSI_RESET\n")
            relatedEntities[i].second.map { relatedEntitiesTwo[it]!! }.map { println("$it \n") }
        }
    }
}
