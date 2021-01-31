package search.cli

import kotlin.reflect.full.memberProperties

fun createIndex(entityType: String, entities: Map<String,Entity>): Map<String, MutableMap<String, MutableList<String>>> {
    val entityProperties = Class.forName("search.cli.$entityType").kotlin.memberProperties
            .map { it.name }
    val entityIndex = entityProperties.map { it to mutableMapOf<String, MutableList<String>>() }.toMap()
    entityProperties.map { propertyName ->
        entities.values.map { entity ->
            val entityPropertyValue = entity.javaClass.kotlin.memberProperties
                    .first { it.name == propertyName }
                    .get(entity)
                    .toString()
            //e.g. the {} associated with key "_id"
            val propertyMap = entityIndex[propertyName]!!
            // e.g. if "1" doesn't exist in the map
            if (propertyMap[entityPropertyValue] == null) {
                entityIndex[propertyName]!!.put(entityPropertyValue, mutableListOf(entity._id))
            } else {
                entityIndex[propertyName]!![entityPropertyValue]!!.add(entity._id)
            }
        }
    }
    return entityIndex
}
