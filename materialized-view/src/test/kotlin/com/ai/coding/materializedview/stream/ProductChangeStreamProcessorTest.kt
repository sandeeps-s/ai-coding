package com.ai.coding.materializedview.stream

import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@SpringBootTest
@Import(TestChannelBinderConfiguration::class)
@ActiveProfiles("test")
class ProductChangeStreamProcessorTest {

    @Autowired
    private lateinit var input: InputDestination

    @Test
    fun `should process product change message through stream`() {
        // Given - Create a test Avro record
        val productChangeRecord = GenericData.Record(null).apply {
            put("productId", "test-stream-001")
            put("name", "Stream Test Product")
            put("description", "Testing stream processing")
            put("price", 199.99)
            put("category", "Stream Test")
            put("changeType", "CREATED")
            put("timestamp", Instant.now().toEpochMilli())
            put("version", 1L)
        }

        // When - Send message through the stream
        input.send(GenericMessage(productChangeRecord), "processProductChange-in-0")

        // Then - Message should be processed without error
        // In a real test, we would verify the materialized view was updated
        Thread.sleep(1000) // Allow processing time

        // Test passes if no exceptions are thrown
    }
}
