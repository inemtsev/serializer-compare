package com.compare

import com.compare.dto.SmallObject
import com.compare.dto.LargeObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 5, time = 5)
@Fork(2)
open class KxSerializationBenchmark {

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = false
        prettyPrint = false
        coerceInputValues = false
    }

    private val smallObj = SmallObject()
    private val largeObj = LargeObject()

    private val smallJson = json.encodeToString(smallObj)
    private val largeJson = json.encodeToString(largeObj)

    @Benchmark
    fun serializeSmallObject(hole: Blackhole) {
        hole.consume(json.encodeToString(smallObj))
    }

    @Benchmark
    fun deserializeSmallObject(hole: Blackhole) {
        hole.consume(json.decodeFromString<SmallObject>(smallJson))
    }

    @Benchmark
    fun serializeLargeObject(hole: Blackhole) {
        hole.consume(json.encodeToString(largeObj))
    }

    @Benchmark
    fun deserializeLargeObject(hole: Blackhole) {
        hole.consume(json.decodeFromString<LargeObject>(largeJson))
    }
}
