# 0002 — Modular hexagonal architecture

Status: Accepted
Date: 2025-08-19

Context
- We need clear separation of concerns, high testability, and replaceable infrastructure for a message-driven materialized view service.

Decision
- Adopt hexagonal (ports and adapters) architecture across four Gradle modules:
  - domain: entities, value objects, domain services, domain events, and ports (inbound/outbound).
  - application: application services implementing inbound use cases and orchestrating domain services.
  - infrastructure: inbound/outbound adapters (web, messaging, persistence), cross-cutting utilities.
  - boot: Spring Boot app, configuration, wiring between ports and adapters, operational concerns.
- Wire dependencies in boot via a configuration class to avoid direct cross-module coupling.

Evidence
- Hex wiring: boot/src/main/kotlin/.../config/HexagonalArchitectureConfig.kt
- Ports: domain/src/main/kotlin/.../port/inbound/ProductUseCase.kt and .../port/outbound/ProductRepository.kt
- Application services: application/src/main/kotlin/.../application/service/*ApplicationService.kt
- Adapters: infrastructure/.../adapter/**

Consequences
- Pros: Clean boundaries, easy testing/mocking at each layer, infrastructure can be swapped.
- Cons: Slightly more boilerplate and wiring code between modules.

Related
- 0003 — Messaging and Avro for ingestion
- 0004 — Aerospike for persistence

