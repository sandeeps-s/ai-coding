package com.ai.coding.materializedview.consumer

import org.apache.avro.generic.GenericRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ProductChangeConsumer {

    private val logger = LoggerFactory.getLogger(ProductChangeConsumer::class.java)

    @KafkaListener(
        topics = ["product-changes"],
        groupId = "materialized-view-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun handleProductChange(
        @Payload productChangeRecord: GenericRecord,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        acknowledgment: Acknowledgment
    ) {
        try {
            val productId = productChangeRecord.get("productId").toString()
            val changeType = productChangeRecord.get("changeType").toString()

            logger.info(
                "Received ProductChange Avro event: productId={}, changeType={}, topic={}, partition={}, offset={}",
                productId,
                changeType,
                topic,
                partition,
                offset
            )

            // Process the product change event
            processProductChange(productChangeRecord)

            // Acknowledge the message after successful processing
            acknowledgment.acknowledge()

            logger.debug("Successfully processed ProductChange for productId: {}", productId)

        } catch (e: Exception) {
            logger.error("Failed to process ProductChange Avro event: {}", productChangeRecord, e)
            // Don't acknowledge on error - message will be retried based on configuration
            throw e
        }
    }

    private fun processProductChange(productChangeRecord: GenericRecord) {
        val productId = productChangeRecord.get("productId").toString()
        val name = productChangeRecord.get("name").toString()
        val description = productChangeRecord.get("description")?.toString()
        val price = productChangeRecord.get("price") as Double
        val category = productChangeRecord.get("category").toString()
        val changeType = productChangeRecord.get("changeType").toString()
        val timestamp = Instant.ofEpochMilli(productChangeRecord.get("timestamp") as Long)
        val version = productChangeRecord.get("version") as Long

        when (changeType) {
            "CREATED" -> {
                logger.info("Processing product creation: productId={}, name={}, price={}, category={}",
                    productId, name, price, category)
                // TODO: Implement materialized view update for product creation
            }
            "UPDATED" -> {
                logger.info("Processing product update: productId={}, name={}, price={}, category={}",
                    productId, name, price, category)
                // TODO: Implement materialized view update for product update
            }
            "DELETED" -> {
                logger.info("Processing product deletion: productId={}", productId)
                // TODO: Implement materialized view update for product deletion
            }
            else -> {
                logger.warn("Unknown change type: {} for productId: {}", changeType, productId)
            }
        }

        logger.debug("Product change details: description={}, timestamp={}, version={}",
            description, timestamp, version)
    }
}
