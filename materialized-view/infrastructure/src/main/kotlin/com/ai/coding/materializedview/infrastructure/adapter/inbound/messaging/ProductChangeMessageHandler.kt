package com.ai.coding.materializedview.infrastructure.adapter.inbound.messaging

import com.ai.coding.materializedview.domain.exception.InvalidMessageException
import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import com.ai.coding.materializedview.infrastructure.adapter.shared.InputSanitizer
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.apache.avro.generic.GenericRecord
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Handles the core processing of product change messages with resilience policies applied.
 * Separated from the adapter to ensure AOP proxies intercept calls (no self-invocation).
 */
@Component
class ProductChangeMessageHandler(
    private val productCommandUseCase: ProductCommandUseCase
) {

    @CircuitBreaker(name = "product-commands")
    fun handleMessage(record: GenericRecord) {
        val changeTypeRaw = record.get("changeType")?.toString()
            ?: throw InvalidMessageException("Missing change type")
        val changeType = InputSanitizer.sanitizeText(changeTypeRaw)?.uppercase()
            ?: throw InvalidMessageException("Invalid change type")

        val product = mapToProduct(record)
        when (changeType) {
            "CREATED" -> productCommandUseCase.createProduct(product)
            "UPDATED" -> productCommandUseCase.updateProduct(product)
            "DELETED" -> productCommandUseCase.deleteProduct(product.productId)
            else -> throw InvalidMessageException("Unknown change type: $changeType")
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

            val productId = InputSanitizer.sanitizeText(record.get("productId")?.toString())
                ?.takeIf { it.isNotBlank() }
                ?: throw InvalidMessageException("Missing productId")
            val name = InputSanitizer.sanitizeText(record.get("name")?.toString())
                ?.takeIf { it.isNotBlank() }
                ?: throw InvalidMessageException("Missing name")

            val description = InputSanitizer.sanitizeText(record.get("description")?.toString())
            val category = InputSanitizer.sanitizeText(record.get("category")?.toString())

            return Product(
                productId = ProductId.of(productId),
                name = ProductName.of(name),
                description = description,
                price = price,
                category = category,
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

