package com.ai.coding.materializedview.infrastructure.adapter.inbound.messaging

import com.ai.coding.materializedview.domain.exception.InvalidMessageException
import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.infrastructure.adapter.shared.InputSanitizer
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import com.ai.coding.materializedview.avro.ProductChange
import com.ai.coding.materializedview.avro.ChangeType
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Handles the core processing of product change messages with resilience policies applied.
 * Separated from the adapter to ensure AOP proxies intercept calls (no self-invocation).
 */
@Component
class ProductChangeMessageHandler(
    private val productCommandUseCase: ProductCommandUseCase,
    private val productQueryUseCase: ProductQueryUseCase
) {

    @CircuitBreaker(name = "product-commands")
    fun handleMessage(record: ProductChange) {
        val changeType = record.changeType
            ?: throw InvalidMessageException("Missing change type")

        when (changeType) {
            ChangeType.CREATED -> productCommandUseCase.createProduct(mapToProductForCreate(record))
            ChangeType.UPDATED -> productCommandUseCase.updateProduct(mapToProductForUpdate(record))
            ChangeType.DELETED -> productCommandUseCase.deleteProduct(ProductId.of(safeText(record.productId)))
        }
    }

    private fun mapToProductForCreate(record: ProductChange): Product {
        val productId = safeText(record.productId)
        val name = safeText(record.name)
        val description = InputSanitizer.sanitizeText(record.description)
        val category = InputSanitizer.sanitizeText(record.category)
        val price = Price.of(record.price)
        val version = record.version
        val instant = Instant.ofEpochMilli(record.timestamp)
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
    }

    private fun mapToProductForUpdate(record: ProductChange): Product {
        val productId = ProductId.of(safeText(record.productId))
        val existing = productQueryUseCase.getProductById(productId)
            ?: throw InvalidMessageException("Product with ID ${'$'}{productId.value} not found for update")
        val name = ProductName.of(safeText(record.name))
        val description = InputSanitizer.sanitizeText(record.description)
        val category = InputSanitizer.sanitizeText(record.category)
        val price = Price.of(record.price)
        val version = record.version
        val instant = Instant.ofEpochMilli(record.timestamp)
        // Preserve createdAt from existing; update only updatedAt
        return existing.copy(
            name = name,
            description = description,
            price = price,
            category = category,
            updatedAt = instant,
            version = version
        )
    }

    private fun safeText(value: String?): String = InputSanitizer.sanitizeText(value)
        ?.takeIf { it.isNotBlank() }
        ?: throw InvalidMessageException("Missing required text field")
}
