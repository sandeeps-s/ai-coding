# 0004 — Aerospike for persistence

Status: Accepted
Date: 2025-08-19

Context
- The service needs a scalable, low-latency data store for materialized product views, supporting flexible queries and high availability.

Decision
- Use Aerospike as the primary database, integrated via Spring Data Aerospike and Aerospike client libraries.
- Outbound persistence adapter (AerospikeProductRepositoryAdapter) implements the ProductRepository port, mapping domain objects to Aerospike entities.
- Repository wiring and configuration handled in boot/config/HexagonalArchitectureConfig.kt and boot/build.gradle.kts.
- Embedded Aerospike used for integration testing.

Evidence
- Adapter: infrastructure/.../adapter/outbound/persistence/AerospikeProductRepositoryAdapter.kt
- Entity: infrastructure/.../adapter/outbound/persistence/entity/ProductView.kt
- Repository: infrastructure/.../adapter/outbound/persistence/repository/ProductViewRepository.kt
- Test: boot/build.gradle.kts (embedded Aerospike)

Consequences
- Pros: High performance, flexible queries, easy integration with Spring Data.
- Cons: Requires explicit entity mapping and operational expertise.

Related
- 0002 — Modular hexagonal architecture
- 0003 — Messaging and Avro for ingestion

