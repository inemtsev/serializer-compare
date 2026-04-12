package com.compare

import com.compare.dto.SmallObjectJackson
import com.compare.dto.LargeObjectJackson
import com.compare.dto.NestedObjectJackson
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.databind.SerializationFeature
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 5, time = 5)
@Fork(2)
open class Jackson2Benchmark {

    private val mapper: ObjectMapper = ObjectMapper().apply {
        registerKotlinModule()
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    private val smallObj = SmallObjectJackson()
    private val largeObj = createLargeObjectJackson()

    private val smallJson = mapper.writeValueAsString(smallObj)
    private val largeJson = mapper.writeValueAsString(largeObj)

    private fun createLargeObjectJackson(): LargeObjectJackson {
        return LargeObjectJackson(
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
            nested = NestedObjectJackson(),
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
            nestedList = listOf(NestedObjectJackson(), NestedObjectJackson(id = 2, value = "second"))
        )
    }

    @Benchmark
    fun serializeSmallObject(hole: Blackhole) {
        hole.consume(mapper.writeValueAsString(smallObj))
    }

    @Benchmark
    fun deserializeSmallObject(hole: Blackhole) {
        hole.consume(mapper.readValue(smallJson, SmallObjectJackson::class.java))
    }

    @Benchmark
    fun serializeLargeObject(hole: Blackhole) {
        hole.consume(mapper.writeValueAsString(largeObj))
    }

    @Benchmark
    fun deserializeLargeObject(hole: Blackhole) {
        hole.consume(mapper.readValue(largeJson, LargeObjectJackson::class.java))
    }
}
