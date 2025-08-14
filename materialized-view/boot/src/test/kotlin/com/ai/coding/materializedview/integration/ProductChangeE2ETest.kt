package com.ai.coding.materializedview.integration

import com.ai.coding.materializedview.MaterializedViewApplication
import io.restassured.RestAssured
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cloud.stream.binder.test.EnableTestBinder
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.messaging.support.GenericMessage
import java.time.Instant

@SpringBootTest(
    classes = [MaterializedViewApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.cloud.function.definition=processProductChange",
        "spring.main.allow-bean-definition-overriding=true"
    ]
)
@EnableTestBinder
class ProductChangeE2ETest {
    @Autowired
    private lateinit var input: InputDestination

    @LocalServerPort
    var port: Int = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    // Test data factory using functional approach
    private fun createTestSchema(): Schema = Schema.Parser().parse("""
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
        """.trimIndent())

    // Functional test data builders
    private fun createProductChangeRecord(
        schema: Schema,
        productId: String = "test-stream-002",
        name: String = "Stream Test Product",
        description: String? = "Testing stream processing",
        price: Double = 199.99,
        category: String = "Stream Test",
        changeType: String = "CREATED",
        timestamp: Long = Instant.now().toEpochMilli(),
        version: Long = 1L
    ): GenericData.Record = GenericData.Record(schema).apply {
        put("productId", productId)
        put("name", name)
        put("description", description)
        put("price", price)
        put("category", category)
        put("changeType", GenericData.EnumSymbol(schema.getField("changeType").schema(), changeType))
        put("timestamp", timestamp)
        put("version", version)
    }

    @Test
    fun `should process product creation message through stream`() {
        // Given - Using functional approach to create test data
        val schema = createTestSchema()
        val productChangeRecord = createProductChangeRecord(
            schema = schema,
            changeType = "CREATED"
        )

        // When - Send message through functional pipeline
        input.send(GenericMessage(productChangeRecord))

        // Then - Validate functional processing completed successfully
        Thread.sleep(1000) // Allow time for async processing
        assertNotNull(input, "InputDestination should be available for testing")

        RestAssured.given()
            .get("/api/products/test-stream-002")
            .then()
            .assertThat()
            .statusCode(200)
            .body("productId", equalTo("test-stream-002"))
            .body("name", equalTo("Stream Test Product"))
            .body("description", equalTo("Testing stream processing"))
            .body("price", equalTo(199.99f))
            .body("category", equalTo("Stream Test"))
            .body("version", equalTo(1));
    }
}
