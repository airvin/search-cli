package search.cli

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

fun loadFile(entity: String, fileName: String): Map<String, Entity> = try {

    val entityClass = Class.forName("search.cli.$entity")

    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(List::class.java, entityClass)
    val jsonAdapter: JsonAdapter<List<Entity>> = moshi.adapter(type)

    val entitiesJson = entityClass::class.java.getResource("/$fileName.json").readText(Charsets.UTF_8)
    val entities = jsonAdapter.fromJson(entitiesJson)!!
    entities.map { it._id to it }.toMap()
} catch (e: Exception) {
    println("Error loading data for $fileName: ${e.message}")
    hashMapOf()
}
