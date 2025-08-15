package com.ai.coding.materializedview.infrastructure.adapter.inbound.messaging

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import org.apache.avro.generic.GenericRecord
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import java.time.Instant
import java.math.BigDecimal
import java.util.function.Consumer

/**
 * Messaging adapter for processing Kafka product change events
 * This is an inbound adapter that converts messages to domain operations
 */
@Configuration
class ProductChangeMessageAdapter(
    private val productCommandUseCase: ProductCommandUseCase
) {

    @Bean
    fun processProductChange(): Consumer<Message<GenericRecord>> {
        return Consumer { message ->
            try {
                val record = message.payload
                val product = mapToProduct(record)
                val changeType = record.get("changeType").toString()

                when (changeType) {
                    "CREATED" -> productCommandUseCase.createProduct(product)
                    "UPDATED" -> productCommandUseCase.updateProduct(product)
                    "DELETED" -> productCommandUseCase.deleteProduct(product.productId)
                    else -> throw IllegalArgumentException("Unknown change type: $changeType")
                }
            } catch (e: Exception) {
                throw RuntimeException("Failed to process product change message", e)
            }
        }
    }

    private fun mapToProduct(record: GenericRecord): Product {
        val timestamp = record.get("timestamp") as Long
        val instant = Instant.ofEpochMilli(timestamp)
        val priceDouble = record.get("price") as? Double
        val price = priceDouble?.let { BigDecimal.valueOf(it) }

        return Product(
            productId = record.get("productId").toString(),
            name = record.get("name").toString(),
            description = record.get("description")?.toString(),
            price = price,
            category = record.get("category")?.toString(),
            createdAt = instant,
            updatedAt = instant,
            version = record.get("version") as Long
        )
    }
}
