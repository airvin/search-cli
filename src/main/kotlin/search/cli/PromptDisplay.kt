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
    val ansi_cyan = "\u001B[36m"
    val ansi_green = "\u001B[32m";
    val ansi_reset = "\u001B[0m"
    val ansi_blue = "\u001B[34m";
    val ansi_purple = "\u001B[35m";

    val header = if (searchValue == "NULL_OR_EMPTY") {
        "\n${ansi_cyan}Searching ${entityType.toString().toLowerCase().capitalize()} " +
                "with an empty $searchTerm field returned ${matchedEntities.size} results $ansi_reset \n"

    } else {
        "\n${ansi_cyan}Searching ${entityType.toString().toLowerCase().capitalize()} " +
                "by $searchTerm $searchValue returned ${matchedEntities.size} results $ansi_reset \n"
    }
    println(header)

    if (matchedEntities.isNotEmpty()) {

        val (relatedTypeOne, relatedTypeTwo) = when (entityType) {
            EntityEnum.ORGANIZATION -> listOf("Users", "Tickets")
            EntityEnum.USER -> listOf("Organizations", "Tickets")
            EntityEnum.TICKET -> listOf("Organizations", "Users")
        }

        matchedEntities.mapIndexed { i, it ->
            println("$ansi_green********************${entityType.toString().toLowerCase().capitalize()} ${i+1}********************$ansi_reset\n")
            println("$it \n")

            if (relatedEntities[i].first.size > 0 ) println("$ansi_blue----------Related ${relatedTypeOne}----------$ansi_reset\n")
            relatedEntities[i].first.map { relatedEntitiesOne[it]!! }.map { println("$it \n") }

            if (relatedEntities[i].second.size > 0 ) println("$ansi_purple----------Related ${relatedTypeTwo}----------$ansi_reset\n")
            relatedEntities[i].second.map { relatedEntitiesTwo[it]!! }.map { println("$it \n") }
        }
    }
}
