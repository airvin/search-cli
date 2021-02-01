package search.cli

import arrow.core.*
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.traverse.traverse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


/**
 * loadFiles coordinates the reading the data files into maps for each entity type.
 *
 * It uses the EntityEnum class to determine which entities should be loaded. Therefore,
 * no changes are required to this function if new entity types are added to the application.
 *
 * @return      Returns an Either that is Left in the case that loading any of the entities from
 *              file failed, or a Right with the list of entity maps in the successful case.
 */
fun loadFiles()
: Either<Error, List<Map<String, Entity>>> {
    // Process the entity names to get a pair of (className, fileName)
    val entities = EntityEnum.values.map { Pair(it, it.toLowerCase() + "s") }

    // Load files to get a List<Either<Error, Map>> and traverse over the list to convert to Either<Error, List<Map>>
    return entities.map { loadFile(it.first, it.second) }
            .traverse(Either.applicative<Error>(), ::identity)
            .fix().map { it.fix() }
}


/**
 * loadFile takes the entity class name (e.g. "Organization", "User" or "Ticket") and file name
 * and loads the list of entities from the json file in the src/main/resources directory, using the
 * Moshi library to convert the json string to kotlin objects.
 *
 * It uses reflection to get the entity class at runtime without having to define a separate function
 * for each entity. Therefore, no changes are required to this function if the entity types change.
 *
 * The list of entities is converted to a map with the id field as a key to enable constant time
 * lookup of entities by their unique identifier.
 *
 * @param entity       The entity class name (e.g. "Organization")
 * @param fileName      The filename for the json file that contains the entity data. For example,
 *                      to load "organization.json" from resources, fileName should be "organization"
 *
 * @return      This function returns an Either with an Error if there is no class of the given
 *              entity name in the classpath, if the file cannot be found, or if the file contains
 *              an invalid json object for the given class. If successful, it will return a Right
 *              with the entity map.
 */
fun loadFile(entity: String, fileName: String): Either<Error, Map<String, Entity>> = try {

    // Get the kotlin class for the entity
    val entityClass = Class.forName("search.cli.$entity")

    // Create an adapter for creating an entity object from a json string
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(List::class.java, entityClass)
    val jsonAdapter: JsonAdapter<List<Entity>> = moshi.adapter(type)

    // Read in the text from the file and convert to a list of entities
    val entitiesJson = entityClass::class.java.getResource("/$fileName.json").readText(Charsets.UTF_8)
    val entities = jsonAdapter.fromJson(entitiesJson)!!

    // Convert the list to a map using id as key and entity object as a value
    Right(entities.map { it.id to it }.toMap())

} catch (e: ClassNotFoundException) {
    Left(Error("Entity class $entity does not exist: ${e.message}"))

} catch (e: IllegalStateException) {
    Left(Error("Invalid file name $fileName.json: ${e.message}"))

} catch (e: JsonDataException) {
    Left(Error("Failed to parse ${entity}s in $fileName.json: ${e.message}"))
}
