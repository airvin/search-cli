package search.cli

import arrow.core.*
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.traverse.traverse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


fun loadFiles()
: Either<Error, List<Map<String, Entity>>> {
    // Process the entity names to get a pair of (className, fileName)
    val entities = EntityEnum.values.map { Pair(it.toLowerCase().capitalize(), it.toLowerCase() + "s") }

    // Load files to get a List<Either<Error, Map>> and traverse over the list to convert to Either<Error, List<Map>>
    return entities.map { loadFile(it.first, it.second) }
            .traverse(Either.applicative<Error>(), ::identity)
            .fix().map { it.fix() }
}


fun loadFile(entity: String, fileName: String): Either<Error, Map<String, Entity>> = try {

    val entityClass = Class.forName("search.cli.$entity")

    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(List::class.java, entityClass)
    val jsonAdapter: JsonAdapter<List<Entity>> = moshi.adapter(type)

    val entitiesJson = entityClass::class.java.getResource("/$fileName.json").readText(Charsets.UTF_8)
    val entities = jsonAdapter.fromJson(entitiesJson)!!
    Right(entities.map { it._id to it }.toMap())

} catch (e: ClassNotFoundException) {
    Left(Error("Entity class $entity does not exist: ${e.message}"))

} catch (e: IllegalStateException) {
    Left(Error("Invalid file name $fileName.json: ${e.message}"))

} catch (e: JsonDataException) {
    Left(Error("Failed to parse ${entity}s in $fileName.json: ${e.message}"))
}
