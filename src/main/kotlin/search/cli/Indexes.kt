package search.cli

import arrow.core.*
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.traverse.traverse
import kotlin.reflect.full.memberProperties


fun createIndexes(
        entityMaps: List<Map<String, Entity>>
): Either<Error, List<Map<String, MutableMap<String, MutableList<String>>>>> {

    // Process entity names to get the class names
    val entityClassNames = EntityEnum.values.map { it }

    // Create the index maps as a  List<Either<Error, Map>> and traverse over the list to convert to Either<Error, List<Map>>
    return entityMaps.mapIndexed { i, entityMap -> createIndex(entityClassNames[i], entityMap) }
            .traverse(Either.applicative<Error>(), ::identity)
            .fix().map { it.fix() }
}


/* 
The function createIndex is used to create a map of maps, where each property of
an entity is indexed to allow searching entities on any property in constant
time. The properties of the entity class are the key of the map, which contains
another map as its value. This inner map has all the unique property values as
the key, with a list of ids of the entities that have that value for its
property. For example, organizationIndex would look something like: 

"_id": {
    "101": ["101"],
    "102": ["102"],
    ...
},
url": {
    "http://initech.zendesk.com/api/v2/organizations/101.json": ["101"],
    "http://initech.zendesk.com/api/v2/organizations/102.json": ["102"],
    ...
},
"details": {
    "Artisan": ["111", "115", "116", "117"],
    ...
},
...

Note that none of the entity types or their properties are hardcoded in this
function. It relies on reflection to be able to inspect the class during
runtime.
 */
fun createIndex(
        entityType: String, entities: Map<String,Entity>
): Either<Error, Map<String, MutableMap<String, MutableList<String>>>> = try {

    // Get the list of properties for the specified entity class (e.g. "_id", "url", etc.)
    val entityProperties = Class.forName("search.cli.$entityType").kotlin.memberProperties
            .map { it.name }

    // Initialise the outside map with the property names as keys
    val entityIndex = entityProperties.map { it to mutableMapOf<String, MutableList<String>>() }.toMap()

    // For each entity, add the entity's property value and id to the inner map
    entities.values.map { entity ->
        entityProperties.map { propertyName ->

            // Find the value associated with the property
            val entityPropertyValue = entity.javaClass.kotlin.memberProperties
                    .first { it.name == propertyName }
                    .get(entity)

            // If the property value is a Collection, create an entry for
            // each element in the collection, (e.g. for each tag in tags)
            if (entityPropertyValue is Collection<*>) {
                entityPropertyValue.map {
                    addPropertyValueToIndexMap(entityIndex, propertyName, it.toString(), entity._id)
                }
            // Also check if the value is missing for that entity
            } else if (entityPropertyValue == null || entityPropertyValue.toString().isEmpty()) {
                addPropertyValueToIndexMap(entityIndex, propertyName, "NULL_OR_EMPTY", entity._id)
            } else {
                addPropertyValueToIndexMap(entityIndex, propertyName, entityPropertyValue.toString(), entity._id)
            }
        }
    }
    Right(entityIndex)

} catch (e: ClassNotFoundException) {
    Left(Error("Entity class $entityType does not exist: ${e.message}"))
}


fun addPropertyValueToIndexMap(
        entityIndex: Map<String, MutableMap<String, MutableList<String>>>,
        propertyName: String,
        value: String,
        id: String
) {
    // Check if the inner map already contains the value
    if (entityIndex[propertyName]!![value] == null) {
        // If not, add the value as a key, with a mutable list with the entity id.
        // This needs to be mutable as more entity ids might need to be added later
        entityIndex[propertyName]!!.put(value, mutableListOf(id))
    } else {
        // If the value exists already, add the entity id to the list
        entityIndex[propertyName]!![value]!!.add(id)
    }
}
