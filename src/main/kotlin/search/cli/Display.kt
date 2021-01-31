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
    println("\nSearching $entityType by $searchTerm $searchValue returned ${matchedEntities.size} results\n")
    if (matchedEntities.size > 0) {
        val (relatedTypeOne, relatedTypeTwo) = when (entityType) {
            EntityEnum.ORGANIZATION -> listOf("Users, Tickets")
            EntityEnum.USER -> listOf("Organizations", "Tickets")
            EntityEnum.TICKET -> listOf("Organizations", "Users")
        }
        matchedEntities.mapIndexed { i, it ->
            println("********************${entityType.toString().toLowerCase().capitalize()} ${i+1}********************\n")
            println("$it \n")
            println("----------Related ${relatedTypeOne}----------\n")
            relatedEntities[i].first.map { relatedEntitiesOne[it]!! }.map { println("$it \n") }
            println("----------Related ${relatedTypeTwo}----------\n")
            relatedEntities[i].second.map { relatedEntitiesTwo[it]!! }.map { println("$it \n") }
        }
    }
}
