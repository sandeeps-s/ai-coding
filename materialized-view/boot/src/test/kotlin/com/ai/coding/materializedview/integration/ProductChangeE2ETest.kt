package com.ai.coding.materializedview.integration

import com.ai.coding.materializedview.MaterializedViewApplication
import com.ai.coding.materializedview.avro.ChangeType
import com.ai.coding.materializedview.avro.ProductChange
import io.restassured.RestAssured
import org.hamcrest.CoreMatchers.equalTo
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

    private fun createProductChange(
        productId: String = "test-stream-002",
        name: String = "Stream Test Product",
        description: String? = "Testing stream processing",
        price: Double = 199.99,
        category: String = "Stream Test",
        changeType: ChangeType = ChangeType.CREATED,
        timestamp: Long = Instant.now().toEpochMilli(),
        version: Long = 1L
    ): ProductChange = ProductChange.newBuilder()
        .setProductId(productId)
        .setName(name)
        .setDescription(description)
        .setPrice(price)
        .setCategory(category)
        .setChangeType(changeType)
        .setTimestamp(timestamp)
        .setVersion(version)
        .build()

    @Test
    fun `should process product creation message through stream`() {
        // Given - Using functional approach to create test data
        val productChange = createProductChange(changeType = ChangeType.CREATED)

        // When - Send message through functional pipeline
        input.send(GenericMessage(productChange))

        // Then - Validate functional processing completed successfully
        Thread.sleep(1000) // Allow time for async processing
        assertNotNull(input, "InputDestination should be available for testing")

        RestAssured.given()
            .auth().preemptive().basic("user", "password")
            .get("/api/products/test-stream-002")
            .then()
            .assertThat()
            .statusCode(200)
            .body("productId", equalTo("test-stream-002"))
            .body("name", equalTo("Stream Test Product"))
            .body("description", equalTo("Testing stream processing"))
            .body("price", equalTo(199.99f))
            .body("category", equalTo("Stream Test"))
            .body("version", equalTo(1))
    }
}
