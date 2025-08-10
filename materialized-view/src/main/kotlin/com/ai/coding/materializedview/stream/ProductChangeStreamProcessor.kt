package com.ai.coding.materializedview.stream

import org.apache.avro.generic.GenericRecord
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class ProductChangeStreamProcessor {

    private val logger = LoggerFactory.getLogger(ProductChangeStreamProcessor::class.java)

    @Bean
    fun processProductChange(): Consumer<GenericRecord> {
        return Consumer { productChangeRecord ->
            try {
                val productId = productChangeRecord.get("productId").toString()
                val changeType = productChangeRecord.get("changeType").toString()

                logger.info(
                    "Processing ProductChange stream event: productId={}, changeType={}",
                    productId,
                    changeType
                )

                processProductChangeEvent(productChangeRecord)

                logger.debug("Successfully processed ProductChange for productId: {}", productId)

            } catch (e: Exception) {
                logger.error("Failed to process ProductChange event: {}", e.message, e)
                throw e
            }
        }
    }

    private fun processProductChangeEvent(record: GenericRecord) {
        // Business logic for processing the product change
        val productId = record.get("productId")?.toString()
        val name = record.get("name")?.toString()
        val price = record.get("price") as? Double
        val category = record.get("category")?.toString()
        val changeType = record.get("changeType")?.toString()

        logger.info(
            "Processing product change: ID={}, Name={}, Price={}, Category={}, Type={}",
            productId, name, price, category, changeType
        )

        // Here you would implement your materialized view update logic
        // For example: updating a database, cache, or search index
    }
}
