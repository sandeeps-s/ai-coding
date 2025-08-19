# 0005 — Observability and health checks

Status: Accepted
Date: 2025-08-19

Context
- The service must be observable in production, with health endpoints and metrics for reliability and troubleshooting.

Decision
- Use Spring Boot Actuator for health, info, and Prometheus metrics endpoints.
- Implement custom health indicators for Kafka and Aerospike to surface broker and database status.
- Use Micrometer for metrics instrumentation (timers, counters) in message handling and domain operations.
- Structured logging with correlation IDs for traceability, using SLF4J MDC and a filter for HTTP requests.
- Expose Prometheus metrics via /actuator/prometheus.

Evidence
- Health indicators: boot/src/main/kotlin/.../health/KafkaHealthIndicator.kt, AerospikeHealthIndicator.kt
- Metrics: infrastructure/.../adapter/inbound/messaging/ProductChangeMessageHandler.kt
- Logging: boot/src/main/kotlin/.../logging/CorrelationIdFilter.kt
- Actuator config: boot/build.gradle.kts

Consequences
- Pros: Production-grade observability, easy integration with monitoring tools.
- Cons: Custom health checks require maintenance as dependencies evolve.

Related
- 0006 — Security and access control

