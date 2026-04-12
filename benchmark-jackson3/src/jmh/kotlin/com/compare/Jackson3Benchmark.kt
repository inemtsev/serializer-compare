package com.compare

import com.compare.dto.SmallObjectJackson3
import com.compare.dto.LargeObjectJackson3
import com.compare.dto.NestedObjectJackson3
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.SerializationFeature
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 5, time = 5)
@Fork(2)
open class Jackson3Benchmark {

    private val mapper: ObjectMapper = jsonMapper {
        addModule(kotlinModule())
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    }

    private val smallObj = SmallObjectJackson3()
    private val largeObj = createLargeObjectJackson3()

    private val smallJson = mapper.writeValueAsString(smallObj)
    private val largeJson = mapper.writeValueAsString(largeObj)

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

    @Benchmark
    fun serializeSmallObject(hole: Blackhole) {
        hole.consume(mapper.writeValueAsString(smallObj))
    }

    @Benchmark
    fun deserializeSmallObject(hole: Blackhole) {
        hole.consume(mapper.readValue(smallJson, SmallObjectJackson3::class.java))
    }

    @Benchmark
    fun serializeLargeObject(hole: Blackhole) {
        hole.consume(mapper.writeValueAsString(largeObj))
    }

    @Benchmark
    fun deserializeLargeObject(hole: Blackhole) {
        hole.consume(mapper.readValue(largeJson, LargeObjectJackson3::class.java))
    }
}
