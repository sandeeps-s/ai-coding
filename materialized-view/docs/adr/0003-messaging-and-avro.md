# 0003 — Messaging and Avro for ingestion

Status: Accepted
Date: 2025-08-19

Context
- The service ingests product change events from Kafka, requiring robust schema evolution and integration with stream processing.

Decision
- Use Apache Kafka as the event transport, integrated via Spring Cloud Stream and Kafka Streams binder.
- Use Avro for message serialization, with schemas managed in infrastructure/src/main/avro.
- Inbound messaging adapter (ProductChangeMessageAdapter) receives and sanitizes Avro messages, delegates to handler for domain logic.
- Circuit breaker and metrics instrumentation applied to message handling for resilience and observability.
- Dead-letter queue (DLQ) routing for failed messages using StreamBridge and error channels.

Evidence
- Messaging adapter: infrastructure/.../adapter/inbound/messaging/ProductChangeMessageAdapter.kt
- Avro schema: infrastructure/src/main/avro/ProductChange.avsc
- DLQ config: infrastructure/.../adapter/inbound/messaging/DeadLetterConfig.kt
- Resilience: infrastructure/.../adapter/inbound/messaging/ProductChangeMessageHandler.kt

Consequences
- Pros: Strong schema guarantees, scalable ingestion, robust error handling.
- Cons: Avro requires explicit schema management and code generation.

Related
- 0002 — Modular hexagonal architecture
- 0004 — Aerospike for persistence

