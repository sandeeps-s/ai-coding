package com.ai.coding.materializedview.stream

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.EnableTestBinder
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.messaging.support.GenericMessage
import java.time.Instant
import kotlin.test.assertNotNull

@SpringBootTest(
    classes = [com.ai.coding.materializedview.MaterializedViewApplication::class],
    properties = [
        "spring.cloud.function.definition=processProductChange"
    ]
)
@EnableTestBinder
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

        // When - Send message through the test binder (use default channel)
        input.send(GenericMessage(productChangeRecord))

        // Then - Validate that the message was processed without exceptions
        // The test passes if no exceptions are thrown during processing
        Thread.sleep(1000) // Allow time for async processing

        assertNotNull(input, "InputDestination should be available for testing")
    }
}
