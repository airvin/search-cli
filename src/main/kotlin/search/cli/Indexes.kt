package search.cli

import arrow.core.*
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.traverse.traverse
import kotlin.reflect.full.memberProperties


/**
 * createIndexes coordinates the creation of an index map for each entity from the map
 * containing all the entities of that type.
 *
 * It uses the EntityEnum class to determine which entities should be processed. Therefore,
 * no changes are required to this function if new entity types are added to the application.
 *
 * @param entityMaps        A list of all the entity maps that were loaded from files into
 *                          the application. These maps have the entity's unique id as a key.
 * @return      Returns an Either that is Left in the case that the creation of index maps for
 *              any of the entities produced an error, and a Right with the list of index maps
 *              for each entity in the successful case.
 */
fun createIndexes(
        entityMaps: List<Map<String, Entity>>
): Either<Error, List<Map<String, MutableMap<String, MutableList<String>>>>> {

    // Process entity names to get the class names
    val entityClassNames = EntityEnum.values.map { it }

    // Create the index maps as a List<Either<Error, Map>> and
    // traverse over the list to convert to Either<Error, List<Map>>
    return entityMaps.mapIndexed { i, entityMap -> createIndex(entityClassNames[i], entityMap) }
            .traverse(Either.applicative<Error>(), ::identity)
            .fix().map { it.fix() }
}


/**
 * createIndex is used to create a map of maps, where each property of
 * an entity is indexed to allow searching entities on any property in constant
 * time.
 *
 * The properties of the entity class are the key of the map, which contains
 * another map as its value. This inner map has all the unique property values as
 * the key, with a list of ids of the entities that have that value for its
 * property. For example, organizationIndex would look something like:
 * "id": {
 *      "101": ["101"],
 *      102": ["102"],
 *      ...
 * },
 * url": {
 *      "http://initech.zendesk.com/api/v2/organizations/101.json": ["101"],
 *      "http://initech.zendesk.com/api/v2/organizations/102.json": ["102"],
 *      ...
 * },
 * "details": {
 *      "Artisan": ["111", "115", "116", "117"],
 *      ...
 * },
 * ...
 *
 * Note that none of the entity types or their properties are hardcoded in this function.
 * It relies on reflection to be able to inspect the class during runtime. For this reason,
 * no changes are required to this function if the type definitions of the current entities
 * change or if new entities are added to the application.
 *
 * @param entityType       A string representing the entityType (e.g. "Organization")
 * @param entities         A map of all the entities of that type that were loaded into the application,
 *                          with the entity unique identifier as the key.
 * @return      Returns an Either that is Left in the case that the input entityType is not a
 *              valid class on the classpath or the entity class name does not match the type of
 *              entity in the entities map. Returns a Right with the index map in the successful case.
 */
fun createIndex(
        entityType: String, entities: Map<String,Entity>
): Either<Error, Map<String, MutableMap<String, MutableList<String>>>> = try {

    // Get the list of properties for the specified entity class (e.g. "id", "url", etc.)
    val entityProperties = Class.forName("search.cli.$entityType").kotlin.memberProperties
            .map { it.name }

    // Initialise the outer map with the property names as keys (e.g. "id": {"", [""]}, etc.)
    val entityIndex = entityProperties.map { it to mutableMapOf<String, MutableList<String>>() }.toMap()

    // For each entity, add the entity's property value and id to the inner map
    entities.values.map { entity ->
        entityProperties.map { propertyName ->

            // Find the value associated with the property
            val entityPropertyValue = entity.javaClass.kotlin.memberProperties
                    .first { it.name == propertyName }
                    .get(entity)

            // If the property value is a Collection, create an entry for each element in
            // the collection, (e.g. for each tag in tags, each domain in domain_names)
            if (entityPropertyValue is Collection<*>) {
                entityPropertyValue.map {
                    addPropertyValueToIndexMap(entityIndex, propertyName, it.toString(), entity.id)
                }
            // Also check if the value is missing for that entity
            } else if (entityPropertyValue == null || entityPropertyValue.toString().isEmpty()) {
                addPropertyValueToIndexMap(entityIndex, propertyName, "NULL_OR_EMPTY", entity.id)
            } else {
                addPropertyValueToIndexMap(entityIndex, propertyName, entityPropertyValue.toString(), entity.id)
            }
        }
    }
    Right(entityIndex)

} catch (e: ClassNotFoundException) {
    Left(Error("Entity class $entityType does not exist: ${e.message}"))
} catch (e: NoSuchElementException) {
    Left(Error("$entityType does not match the entity type in the provided map: ${e.message}"))
}

/**
 * addPropertyValueToIndexMap takes the partially built entity index map and adds the entity's id to the
 * inner map against the property value.
 *
 * @param entityIndex      The partially built entity index map
 * @param propertyName     The name of the entity class property, which is the key for the outer map (e.g. "name")
 * @param value            The entity's value for that property (e.g. "Jane Smith")
 * @param id               The entity's unique identifier (e.g. "101")
 */
fun addPropertyValueToIndexMap(
        entityIndex: Map<String, MutableMap<String, MutableList<String>>>,
        propertyName: String,
        value: String,
        id: String
) {
    // Check if the inner map already contains a map of the value
    if (entityIndex[propertyName]!![value] == null) {
        // If not, add the value as a key, with a mutable list with the entity id.
        // This needs to be mutable as more entity ids might need to be added later
        entityIndex[propertyName]!!.put(value, mutableListOf(id))
    } else {
        // If the value exists already, add the entity id to the list
        entityIndex[propertyName]!![value]!!.add(id)
    }
}
