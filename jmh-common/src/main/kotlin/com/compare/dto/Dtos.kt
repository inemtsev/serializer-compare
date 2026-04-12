package com.compare.dto

import kotlinx.serialization.Serializable

@Serializable
data class SmallObject(
    val id: Int = 42,
    val name: String = "test-item",
    val active: Boolean = true,
    val timestamp: Long = 1234567890L,
    val score: Double = 3.14159
)

@Serializable
data class LargeObject(
    val id: Int = 1,
    val name: String = "benchmark-large-object",
    val description: String = "This is a comprehensive test object with many fields for thorough benchmarking",
    val active: Boolean = true,
    val count: Int = 1000,
    val amount: Double = 999.99,
    val ratio: Float = 0.5f,
    val timestamp: Long = 1234567890L,
    val category: String = "performance",
    val tags: List<String> = listOf("benchmark", "serialization", "performance", "kotlin"),
    val nested: NestedObject = NestedObject(),
    val metadata: Map<String, String> = mapOf(
        "version" to "1.0",
        "environment" to "test",
        "region" to "us-east-1"
    ),
    val enabled: Boolean = true,
    val priority: Int = 5,
    val weight: Double = 1.5,
    val scale: Float = 2.0f,
    val createdAt: Long = 1609459200L,
    val updatedAt: Long = 1609545600L,
    val status: String = "completed",
    val flag: Boolean = false,
    val threshold: Double = 0.01,
    val multiplier: Float = 1.1f,
    val nestedList: List<NestedObject> = listOf(NestedObject(), NestedObject(id = 2, value = "second"))
)

@Serializable
data class NestedObject(
    val id: Int = 99,
    val value: String = "nested-value",
    val items: List<String> = listOf("item1", "item2", "item3")
)
