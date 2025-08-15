package com.ai.coding.materializedview.domain.exception

/**
 * Domain-centric exception hierarchy to classify failures across layers.
 */
open class DomainException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/** Invalid or unsupported message/data encountered at the boundary. */
class InvalidMessageException(message: String, cause: Throwable? = null) : DomainException(message, cause)

/** Failures caused by external systems (DB, brokers, remote services). */
class ExternalDependencyException(message: String, cause: Throwable? = null) : DomainException(message, cause)

/** Persistence failures in infrastructure mappings/adapters. */
class PersistenceException(message: String, cause: Throwable? = null) : DomainException(message, cause)

/** Generic processing failure when classifying is not possible. */
class ProcessingException(message: String, cause: Throwable? = null) : DomainException(message, cause)

