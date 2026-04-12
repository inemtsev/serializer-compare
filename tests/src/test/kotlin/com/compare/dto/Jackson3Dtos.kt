package com.compare.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SmallObjectJackson3(
    @param:JsonProperty("id") val id: Int = 42,
    @param:JsonProperty("name") val name: String = "test-item",
    @param:JsonProperty("active") val active: Boolean = true,
    @param:JsonProperty("timestamp") val timestamp: Long = 1234567890L,
    @param:JsonProperty("score") val score: Double = 3.14159
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LargeObjectJackson3(
@param:JsonProperty("id") val id: Int = 1,
    @param:JsonProperty("name") val name: String = "benchmark-large-object",
    @param:JsonProperty("description") val description: String = "This is a comprehensive test object with many fields for thorough benchmarking",
    @param:JsonProperty("active") val active: Boolean = true,
    @param:JsonProperty("count") val count: Int = 1000,
    @param:JsonProperty("amount") val amount: Double = 999.99,
    @param:JsonProperty("ratio") val ratio: Float = 0.5f,
    @param:JsonProperty("timestamp") val timestamp: Long = 1234567890L,
    @param:JsonProperty("category") val category: String = "performance",
    @param:JsonProperty("tags") val tags: List<String> = listOf("benchmark", "serialization", "performance", "kotlin"),
    @param:JsonProperty("nested") val nested: NestedObjectJackson3 = NestedObjectJackson3(),
    @param:JsonProperty("metadata") val metadata: Map<String, String> = mapOf(
        "version" to "1.0",
        "environment" to "test",
        "region" to "us-east-1"
    ),
    @param:JsonProperty("enabled") val enabled: Boolean = true,
    @param:JsonProperty("priority") val priority: Int = 5,
    @param:JsonProperty("weight") val weight: Double = 1.5,
    @param:JsonProperty("scale") val scale: Float = 2.0f,
    @param:JsonProperty("createdAt") val createdAt: Long = 1609459200L,
    @param:JsonProperty("updatedAt") val updatedAt: Long = 1609545600L,
    @param:JsonProperty("status") val status: String = "completed",
    @param:JsonProperty("flag") val flag: Boolean = false,
    @param:JsonProperty("threshold") val threshold: Double = 0.01,
    @param:JsonProperty("multiplier") val multiplier: Float = 1.1f,
    @param:JsonProperty("nestedList") val nestedList: List<NestedObjectJackson3> = listOf(NestedObjectJackson3(), NestedObjectJackson3(id = 2, value = "second"))
)

data class NestedObjectJackson3(
    @param:JsonProperty("id") val id: Int = 99,
    @param:JsonProperty("value") val value: String = "nested-value",
    @param:JsonProperty("items") val items: List<String> = listOf("item1", "item2", "item3")
)
