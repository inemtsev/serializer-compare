# Project Summary

## What was built

A Kotlin project with JMH benchmarks to compare:
- kotlinx.serialization
- Jackson 2.x
- Jackson 3.x

The benchmark coverage includes:
- small object serialization
- small object deserialization
- large object serialization
- large object deserialization

This gives **12 benchmark cases total** across the 3 serializers.

Versions are managed in `gradle/libs.versions.toml`.

## Project layout

- `tests/` — round-trip smoke tests
- `benchmark-kserialization/` — isolated JMH benchmarks for kotlinx.serialization
- `benchmark-jackson2/` — isolated JMH benchmarks for Jackson 2.x
- `benchmark-jackson3/` — isolated JMH benchmarks for Jackson 3.x
- `jmh-common/` — shared model/fixture code used by benchmarks
- `benchmark-results/` — benchmark run data and visualization viewer

## Important note

Jackson 2 and Jackson 3 are benchmarked in **separate JMH modules/jars** so they do not share the same runtime classpath. This makes the comparison safer and more trustworthy.

## Commands to run

### Build everything
```bash
./gradlew build
```

### Run tests
```bash
./gradlew :tests:test
```

### List available benchmarks
```bash
java -jar benchmark-kserialization/build/libs/benchmark-kserialization-1.0.0-jmh.jar -l
java -jar benchmark-jackson2/build/libs/benchmark-jackson2-1.0.0-jmh.jar -l
java -jar benchmark-jackson3/build/libs/benchmark-jackson3-1.0.0-jmh.jar -l
```

### Run benchmarks with allocation metrics
```bash
java -jar benchmark-kserialization/build/libs/benchmark-kserialization-1.0.0-jmh.jar -prof gc -rf json -rff kx_results.json
java -jar benchmark-jackson2/build/libs/benchmark-jackson2-1.0.0-jmh.jar -prof gc -rf json -rff jackson2_results.json
java -jar benchmark-jackson3/build/libs/benchmark-jackson3-1.0.0-jmh.jar -prof gc -rf json -rff jackson3_results.json
```

Look at `gc.alloc.rate.norm` in the JMH output/results for **bytes allocated per operation**.

## Caveats

- Jackson 3.x is a stable release but uses different Maven artifacts (`tools.jackson.core:*`).

## Verification status

Verified during setup:
- project build passes
- tests pass
- all 3 JMH jars list their benchmarks
