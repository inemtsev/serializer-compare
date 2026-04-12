package com.compare

import com.compare.dto.SmallObject
import com.compare.dto.LargeObject
import com.compare.dto.SmallObjectJackson2
import com.compare.dto.LargeObjectJackson2
import com.compare.dto.NestedObjectJackson2
import com.compare.dto.SmallObjectJackson3
import com.compare.dto.LargeObjectJackson3
import com.compare.dto.NestedObjectJackson3
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.databind.SerializationFeature
import tools.jackson.databind.ObjectMapper as Jackson3ObjectMapper
import tools.jackson.databind.SerializationFeature as Jackson3SerializationFeature
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SmokeTest {

    private val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    }

    private val jackson3ObjectMapper = jsonMapper {
        addModule(kotlinModule())
        disable(Jackson3SerializationFeature.FAIL_ON_EMPTY_BEANS)
    }

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @Test
    fun `kotlinx serialization small round-trip`() {
        val obj = SmallObject()
        val serialized = json.encodeToString(obj)
        val deserialized = json.decodeFromString<SmallObject>(serialized)
        assertEquals(obj, deserialized)
    }

    @Test
    fun `kotlinx serialization large round-trip`() {
        val obj = LargeObject()
        val serialized = json.encodeToString(obj)
        val deserialized = json.decodeFromString<LargeObject>(serialized)
        assertEquals(obj, deserialized)
    }

    @Test
    fun `Jackson 2 small round-trip`() {
        val obj = SmallObjectJackson2()
        val serialized = objectMapper.writeValueAsString(obj)
        val deserialized = objectMapper.readValue(serialized, SmallObjectJackson2::class.java)
        assertEquals(obj, deserialized)
    }

    @Test
    fun `Jackson 2 large round-trip`() {
        val obj = createLargeObjectJackson2()
        val serialized = objectMapper.writeValueAsString(obj)
        val deserialized = objectMapper.readValue(serialized, LargeObjectJackson2::class.java)
        assertEquals(obj, deserialized)
    }

    @Test
    fun `Jackson 3 small round-trip`() {
        val obj = SmallObjectJackson3()
        val serialized = jackson3ObjectMapper.writeValueAsString(obj)
        val deserialized = jackson3ObjectMapper.readValue(serialized, SmallObjectJackson3::class.java)
        assertEquals(obj, deserialized)
    }

    @Test
    fun `Jackson 3 large round-trip`() {
        val obj = createLargeObjectJackson3()
        val serialized = jackson3ObjectMapper.writeValueAsString(obj)
        val deserialized = jackson3ObjectMapper.readValue(serialized, LargeObjectJackson3::class.java)
        assertEquals(obj, deserialized)
    }

    private fun createLargeObjectJackson2(): LargeObjectJackson2 {
        return LargeObjectJackson2(
            id = 1,
            name = "benchmark-large-object",
            description = "This is a comprehensive test object with many fields for thorough benchmarking",
            active = true,
            count = 1000,
            amount = 999.99,
            ratio = 0.5f,
            timestamp = 1234567890L,
            category = "performance",
            tags = listOf("benchmark", "serialization", "performance", "kotlin"),
            nested = NestedObjectJackson2(),
            metadata = mapOf(
                "version" to "1.0",
                "environment" to "test",
                "region" to "us-east-1"
            ),
            enabled = true,
            priority = 5,
            weight = 1.5,
            scale = 2.0f,
            createdAt = 1609459200L,
            updatedAt = 1609545600L,
            status = "completed",
            flag = false,
            threshold = 0.01,
            multiplier = 1.1f,
            nestedList = listOf(NestedObjectJackson2(), NestedObjectJackson2(id = 2, value = "second"))
        )
    }

    private fun createLargeObjectJackson3(): LargeObjectJackson3 {
        return LargeObjectJackson3(
            id = 1,
            name = "benchmark-large-object",
            description = "This is a comprehensive test object with many fields for thorough benchmarking",
            active = true,
            count = 1000,
            amount = 999.99,
            ratio = 0.5f,
            timestamp = 1234567890L,
            category = "performance",
            tags = listOf("benchmark", "serialization", "performance", "kotlin"),
            nested = NestedObjectJackson3(),
            metadata = mapOf(
                "version" to "1.0",
                "environment" to "test",
                "region" to "us-east-1"
            ),
            enabled = true,
            priority = 5,
            weight = 1.5,
            scale = 2.0f,
            createdAt = 1609459200L,
            updatedAt = 1609545600L,
            status = "completed",
            flag = false,
            threshold = 0.01,
            multiplier = 1.1f,
            nestedList = listOf(NestedObjectJackson3(), NestedObjectJackson3(id = 2, value = "second"))
        )
    }
}
