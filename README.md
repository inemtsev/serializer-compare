# Serializer Compare

JMH benchmarks comparing serialization performance of:
- **kotlinx.serialization**
- **Jackson 2.x**
- **Jackson 3.x**

Versions are managed in `gradle/libs.versions.toml`.

Includes minimal Ktor integration context and round-trip smoke tests.

## Project Structure

```
serializer-compare/
├── jmh-common/              # Shared DTOs for kotlinx.serialization
├── benchmark-kserialization/ # JMH benchmarks: kotlinx.serialization (4 cases)
├── benchmark-jackson2/       # JMH benchmarks: Jackson 2.x (4 cases)
├── benchmark-jackson3/       # JMH benchmarks: Jackson 3.x (4 cases)
├── app/                      # Minimal Ktor server (smoke test)
└── tests/                    # Round-trip behavior tests (6 cases)
```

## Requirements

- JDK 21 (toolchain pinned)
- Gradle 8.9+

## Build Commands

```bash
# Full build (all modules)
./gradlew build

# Run tests only
./gradlew :tests:test

# Run Ktor smoke test
./gradlew :app:run
```

## Running Benchmarks

Each benchmark module produces an isolated JMH jar with its own classpath. This ensures Jackson 2 and Jackson 3 benchmarks do not interfere with each other.

```bash
# Build all benchmark jars
./gradlew build

# Run kotlinx.serialization benchmarks
java -jar benchmark-kserialization/build/libs/benchmark-kserialization-1.0.0-jmh.jar -l

# Run Jackson 2 benchmarks
java -jar benchmark-jackson2/build/libs/benchmark-jackson2-1.0.0-jmh.jar -l

# Run Jackson 3 benchmarks
java -jar benchmark-jackson3/build/libs/benchmark-jackson3-1.0.0-jmh.jar -l
```

### Benchmark Cases (12 total)

| Serializer            | Small Object | Large Object |
|-----------------------|--------------|--------------|
| kotlinx.serialization | serialize/deserialize | serialize/deserialize |
| Jackson 2.x           | serialize/deserialize | serialize/deserialize |
| Jackson 3.x           | serialize/deserialize | serialize/deserialize |

### JMH Options for Memory/Performance

Key JMH parameters:

| Parameter | Description |
|-----------|-------------|
| `-gc` | Enable GC profiler (required for allocation metrics) |
| `-prof gc` | Allocation profiling via GC stats |
| `-prof gc,bytes` | Allocation profiling with bytes per op |
| `-rf json` | Results format (json/text) |
| `-rff <file>` | Results output file |
| `-w 3 -wi 3` | Warmup: 3 iterations, 3 seconds each |
| `-r 5 -i 5` | Measurement: 5 iterations, 5 seconds each |
| `-f 2` | Fork count (reduces JIT variance) |

### Understanding Allocation Metrics

Allocation rates are reported via the `gc.alloc.rate.norm` metric in JMH results. This represents **bytes allocated per operation**, normalized to a per-operation basis.

To obtain allocation data:

```bash
# Run benchmarks with GC profiler enabled
java -jar benchmark-kserialization/build/libs/benchmark-kserialization-1.0.0-jmh.jar -gc -prof gc -rf json -rff kx_results.json

java -jar benchmark-jackson2/build/libs/benchmark-jackson2-1.0.0-jmh.jar -gc -prof gc -rf json -rff jackson2_results.json

java -jar benchmark-jackson3/build/libs/benchmark-jackson3-1.0.0-jmh.jar -gc -prof gc -rf json -rff jackson3_results.json
```

The `gc.alloc.rate.norm` column in results shows normalized allocation rate in bytes/op.

### Benchmark Visualization

Open the HTML viewer to compare results visually:

```bash
# Serve locally (from project root)
cd /Users/ilyanemtsev/MyGit/serializer-compare
python3 -m http.server 8080

# Open in browser
open http://localhost:8080/benchmark-results/
```

The viewer supports:
- Selecting a run via `?run=YYYYMMDD-HHMMSS` query param
- Defaults to the latest run if no param given
- Invalid run IDs show a warning and fall back to latest
- Throughput and allocation bar charts
- Tabular summary of all metrics

To run benchmarks and refresh the viewer data:

```bash
./run-all-benchmarks.sh [quick|full]
```

## Ktor Endpoints

When running `app` module:
- `GET /health` - Health check (returns "OK")
- `GET /small` - Serialize small object using kotlinx.serialization
- `GET /large` - Serialize large object using kotlinx.serialization
- `POST /small` - Round-trip small object
- `POST /large` - Round-trip large object

```bash
# Run Ktor server
./gradlew :app:run

# Server starts on port 8080
curl http://localhost:8080/health
curl http://localhost:8080/small
```

## Test Results

```bash
# Round-trip tests for all serializers (6 tests total)
./gradlew :tests:test
```

The `SmokeTest` verifies round-trip serialization for:
- kotlinx.serialization (small and large objects)
- Jackson 2.x (small and large objects)
- Jackson 3.x (small and large objects)

## Caveats & Compromises

### Jackson 3.x
Jackson 3.x is used from the stable 3.1.x line. The Jackson 3.x series uses different Maven artifacts (`tools.jackson.core:*`) but still depends on Jackson 2.x annotations (`com.fasterxml.jackson.core:jackson-annotations`).

### Ktor + Jackson Integration
Ktor's official `ktor-serialization-jackson` plugin targets Jackson 2.x only. For production use with Jackson 3.x, custom serialization setup would be required. The Ktor app in this project uses kotlinx.serialization only.

### Fairness of Comparison
- kotlinx.serialization benchmarks use `encodeDefaults = true` to match Jackson behavior
- All serializers use default configuration (no special tuning)
- DTOs are structurally equivalent but use native annotations per library
- Jackson benchmarks use `registerKotlinModule()` for Kotlin data class support

### Benchmark Characteristics
- Default Fork: 2 (reduces JIT warmup variance)
- Default Warmup: 3 iterations × 3 seconds
- Default Measurement: 5 iterations × 5 seconds
- Mode: Throughput (ops/ms)

## Test Results Interpretation

JMH results include:
- `ops/ms` - Operations per millisecond (throughput)
- `ns/op` - Nanoseconds per operation (latency)
- `gc.alloc.rate.norm` - Normalized allocation rate (bytes/op)
- `±` - Standard deviation

Lower nanoseconds/op, higher ops/ms, and lower allocation rates indicate better performance.
