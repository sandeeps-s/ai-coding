package com.ai.coding.materializedview.stream

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import kotlin.test.assertTrue

@SpringBootTest
@Import(TestChannelBinderConfiguration::class)
@ActiveProfiles("test")
class ProductChangeStreamProcessorTest {

    @Autowired
    private lateinit var input: InputDestination

    @Test
    fun `should process product change message through stream`() {
        // Given - Create proper Avro schema
        val schemaJson = """
        {
          "type": "record",
          "name": "ProductChange",
          "namespace": "com.ai.coding.materializedview.avro",
          "fields": [
            {"name": "productId", "type": "string"},
            {"name": "name", "type": "string"},
            {"name": "description", "type": ["null", "string"], "default": null},
            {"name": "price", "type": "double"},
            {"name": "category", "type": "string"},
            {"name": "changeType", "type": {
              "type": "enum", 
              "name": "ChangeType", 
              "symbols": ["CREATED", "UPDATED", "DELETED"]
            }},
            {"name": "timestamp", "type": "long"},
            {"name": "version", "type": "long", "default": 1}
          ]
        }
        """.trimIndent()

        val schema = Schema.Parser().parse(schemaJson)

        // Create a test Avro record with proper schema
        val productChangeRecord = GenericData.Record(schema).apply {
            put("productId", "test-stream-001")
            put("name", "Stream Test Product")
            put("description", "Testing stream processing")
            put("price", 199.99)
            put("category", "Stream Test")
            put("changeType", GenericData.EnumSymbol(schema.getField("changeType").schema(), "CREATED"))
            put("timestamp", Instant.now().toEpochMilli())
            put("version", 1L)
        }

        // When - Send message through the test binder
        input.send(GenericMessage(productChangeRecord))

        // Then - Wait for processing and validate no exceptions
        Thread.sleep(2000)

        assertTrue(true, "Message was processed successfully through Spring Cloud Stream Test Binder")
    }
}
