package com.ai.coding.materializedview.stream

import org.apache.avro.generic.GenericRecord
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

// Functional data classes for type safety
data class ProductChange(
    val productId: String,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String,
    val changeType: ChangeType,
    val timestamp: Long,
    val version: Long
)

enum class ChangeType { CREATED, UPDATED, DELETED }

// Functional Result type for error handling
sealed class ProcessingResult {
    data class Success(val productChange: ProductChange) : ProcessingResult()
    data class Failure(val error: String, val cause: Throwable?) : ProcessingResult()
}

@Configuration
class ProductChangeStreamProcessor {

    private val logger = LoggerFactory.getLogger(ProductChangeStreamProcessor::class.java)

    @Bean
    fun processProductChange(): Consumer<GenericRecord> =
        Consumer { record ->
            record
                .let(::parseProductChange)
                .let(::validateProductChange)
                .let(::processProductChangeEvent)
                .fold(
                    onSuccess = { productChange ->
                        logger.info("Successfully processed ProductChange for productId: {}", productChange.productId)
                    },
                    onFailure = { error ->
                        logger.error("Failed to process ProductChange event: {}", error.message, error)
                        throw error
                    }
                )
        }

    // Pure function to parse GenericRecord to ProductChange
    private fun parseProductChange(record: GenericRecord): Result<ProductChange> =
        runCatching {
            ProductChange(
                productId = record.get("productId")?.toString() ?: throw IllegalArgumentException("productId is required"),
                name = record.get("name")?.toString() ?: throw IllegalArgumentException("name is required"),
                description = record.get("description")?.toString(),
                price = record.get("price") as? Double ?: throw IllegalArgumentException("price is required"),
                category = record.get("category")?.toString() ?: throw IllegalArgumentException("category is required"),
                changeType = parseChangeType(record.get("changeType")?.toString()),
                timestamp = record.get("timestamp") as? Long ?: throw IllegalArgumentException("timestamp is required"),
                version = record.get("version") as? Long ?: 1L
            )
        }

    // Pure function to parse change type
    private fun parseChangeType(changeType: String?): ChangeType =
        when (changeType?.uppercase()) {
            "CREATED" -> ChangeType.CREATED
            "UPDATED" -> ChangeType.UPDATED
            "DELETED" -> ChangeType.DELETED
            else -> throw IllegalArgumentException("Invalid changeType: $changeType")
        }

    // Pure function for validation
    private fun validateProductChange(result: Result<ProductChange>): Result<ProductChange> =
        result.mapCatching { productChange ->
            require(productChange.productId.isNotBlank()) { "productId cannot be blank" }
            require(productChange.name.isNotBlank()) { "name cannot be blank" }
            require(productChange.price >= 0) { "price cannot be negative" }
            require(productChange.category.isNotBlank()) { "category cannot be blank" }
            productChange
        }

    // Business logic processing using functional composition
    private fun processProductChangeEvent(result: Result<ProductChange>): Result<ProductChange> =
        result.map { productChange ->
            logger.info(
                "Processing product change: ID={}, Name={}, Price={}, Category={}, Type={}",
                productChange.productId,
                productChange.name,
                productChange.price,
                productChange.category,
                productChange.changeType
            )

            // Apply functional transformation based on change type
            when (productChange.changeType) {
                ChangeType.CREATED -> handleProductCreation(productChange)
                ChangeType.UPDATED -> handleProductUpdate(productChange)
                ChangeType.DELETED -> handleProductDeletion(productChange)
            }
        }

    // Higher-order functions for specific business logic
    private fun handleProductCreation(productChange: ProductChange): ProductChange =
        productChange.also {
            logger.debug("Creating product: {}", it.productId)
            // Implement materialized view creation logic here
        }

    private fun handleProductUpdate(productChange: ProductChange): ProductChange =
        productChange.also {
            logger.debug("Updating product: {}", it.productId)
            // Implement materialized view update logic here
        }

    private fun handleProductDeletion(productChange: ProductChange): ProductChange =
        productChange.also {
            logger.debug("Deleting product: {}", it.productId)
            // Implement materialized view deletion logic here
        }
}
