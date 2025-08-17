package com.ai.coding.materializedview.infrastructure.adapter.inbound.messaging

import com.ai.coding.materializedview.domain.exception.InvalidMessageException
import com.ai.coding.materializedview.infrastructure.adapter.shared.ExceptionMapper
import com.ai.coding.materializedview.infrastructure.adapter.shared.InputSanitizer
import org.apache.avro.generic.GenericRecord
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import java.util.UUID
import java.util.function.Consumer

/**
 * Messaging adapter for processing Kafka product change events
 * This is an inbound adapter that converts messages to domain operations
 */
@Configuration
class ProductChangeMessageAdapter(
    private val handler: ProductChangeMessageHandler
) {

    private val log = LoggerFactory.getLogger(ProductChangeMessageAdapter::class.java)
    private val correlationHeader = "X-Correlation-Id"
    private val correlationMdcKey = "correlationId"

    @Bean
    fun processProductChange(): Consumer<Message<GenericRecord>> {
        return Consumer { message ->
            val hdr = message.headers[correlationHeader]
            val rawCorrelation = hdr?.toString()
            val sanitizedCorrelation = InputSanitizer.sanitizeText(rawCorrelation)
            val correlationId = sanitizedCorrelation?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
            MDC.put(correlationMdcKey, correlationId)
            try {
                // Delegate to handler with resilience/AOP applied
                handler.handleMessage(message.payload)
            } catch (ex: InvalidMessageException) {
                // Non-retryable: immediately surface to error channel / DLQ as configured
                log.warn("Discarding invalid message: {}", ex.message)
                throw ex
            } catch (ex: Exception) {
                // Map infra/runtime to domain exception types to drive retry/DLQ behavior
                val mapped = ExceptionMapper.map(ex)
                log.error("Failed to process product change message: {}", mapped.message, mapped)
                // Throw mapped exception to trigger retry/backoff and ultimately DLQ
                throw mapped
            } finally {
                MDC.remove(correlationMdcKey)
            }
        }
    }
}
