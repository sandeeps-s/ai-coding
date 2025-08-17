package com.ai.coding.materializedview.infrastructure.adapter.inbound.messaging

import com.ai.coding.materializedview.domain.exception.ExternalDependencyException
import com.ai.coding.materializedview.domain.exception.InvalidMessageException
import com.ai.coding.materializedview.domain.exception.ProcessingException
import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import com.ai.coding.materializedview.infrastructure.adapter.shared.ExceptionMapper
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.apache.avro.generic.GenericRecord
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import java.time.Instant
import java.util.UUID
import java.util.function.Consumer

/**
 * Messaging adapter for processing Kafka product change events
 * This is an inbound adapter that converts messages to domain operations
 */
@Configuration
class ProductChangeMessageAdapter(
    private val productCommandUseCase: ProductCommandUseCase
) {

    private val log = LoggerFactory.getLogger(ProductChangeMessageAdapter::class.java)
    private val correlationHeader = "X-Correlation-Id"
    private val correlationMdcKey = "correlationId"

    @Bean
    fun processProductChange(): Consumer<Message<GenericRecord>> {
        return Consumer { message ->
            val hdr = message.headers[correlationHeader]
            val correlationId = hdr?.toString()?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
            MDC.put(correlationMdcKey, correlationId)
            try {
                // Route via circuit breaker so downstream infra outages trip/open the circuit
                handleMessage(message.payload)
            } catch (ex: InvalidMessageException) {
                // Non-retryable: immediately surface to error channel / DLQ as configured
                log.warn("Discarding invalid message: ${'$'}{ex.message}")
                throw ex
            } catch (ex: Exception) {
                // Map infra/runtime to domain exception types to drive retry/DLQ behavior
                val mapped = ExceptionMapper.map(ex)
                log.error("Failed to process product change message: ${'$'}{mapped.message}", mapped)
                // Throw mapped exception to trigger retry/backoff and ultimately DLQ
                throw mapped
            } finally {
                MDC.remove(correlationMdcKey)
            }
        }
    }

    @CircuitBreaker(name = "product-commands")
    fun handleMessage(record: GenericRecord) {
        val product = mapToProduct(record)
        val changeType = record.get("changeType")?.toString()
            ?: throw InvalidMessageException("Missing change type")

        when (changeType) {
            "CREATED" -> productCommandUseCase.createProduct(product)
            "UPDATED" -> productCommandUseCase.updateProduct(product)
            "DELETED" -> productCommandUseCase.deleteProduct(product.productId)
            else -> throw InvalidMessageException("Unknown change type: ${'$'}changeType")
        }
    }

    private fun mapToProduct(record: GenericRecord): Product {
        try {
            val timestamp = (record.get("timestamp") as? Number)?.toLong()
                ?: throw InvalidMessageException("Missing or invalid timestamp")
            val instant = Instant.ofEpochMilli(timestamp)

            val priceNum = record.get("price") as? Number
            val price = priceNum?.toDouble()?.let { Price.of(it) }

            val versionNum = record.get("version") as? Number
            val version = versionNum?.toLong() ?: 1L

            val productId = record.get("productId")?.toString()
                ?: throw InvalidMessageException("Missing productId")
            val name = record.get("name")?.toString()
                ?: throw InvalidMessageException("Missing name")

            return Product(
                productId = ProductId.of(productId),
                name = ProductName.of(name),
                description = record.get("description")?.toString(),
                price = price,
                category = record.get("category")?.toString(),
                createdAt = instant,
                updatedAt = instant,
                version = version
            )
        } catch (ex: InvalidMessageException) {
            throw ex
        } catch (ex: Exception) {
            // Any mapping/Avro/casting error is treated as invalid message
            throw InvalidMessageException("Failed to map incoming record to Product", ex)
        }
    }
}
