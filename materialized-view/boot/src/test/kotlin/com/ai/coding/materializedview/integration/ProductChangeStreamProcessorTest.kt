package com.ai.coding.materializedview.integration

import com.ai.coding.materializedview.MaterializedViewApplication
import com.ai.coding.materializedview.avro.ChangeType
import com.ai.coding.materializedview.avro.ProductChange
import com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.repository.ProductViewRepository
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.EnableTestBinder
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.messaging.support.GenericMessage
import java.time.Duration
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
class ProductChangeStreamProcessorTest {

    @Autowired
    private lateinit var input: InputDestination

    @Autowired
    private lateinit var repo: ProductViewRepository

    private fun createProductChange(
        productId: String = "test-stream-001",
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
    fun `should persist product on creation`() {
        val id = "test-create-001"
        val productChange = createProductChange(productId = id, changeType = ChangeType.CREATED, version = 1L)
        input.send(GenericMessage(productChange))

        await().atMost(Duration.ofSeconds(5)).untilAsserted {
            val pv = repo.findById(id)
            assertTrue(pv.isPresent)
            assertEquals("Stream Test Product", pv.get().name)
            assertEquals(199.99, pv.get().price)
            assertEquals(1L, pv.get().version)
        }
    }

    @Test
    fun `should update existing product and preserve createdAt`() {
        val id = "test-update-002"
        // Seed create
        input.send(GenericMessage(createProductChange(productId = id, changeType = ChangeType.CREATED, version = 1L)))
        await().atMost(Duration.ofSeconds(5)).untilAsserted { assertTrue(repo.existsById(id)) }
        val before = repo.findById(id).get()
        val beforeCreatedAt = before.createdAt

        // Send update
        input.send(
            GenericMessage(
                createProductChange(
                    productId = id,
                    name = "Updated Product",
                    price = 299.99,
                    changeType = ChangeType.UPDATED,
                    version = 2L,
                    timestamp = Instant.now().toEpochMilli()
                )
            )
        )

        await().atMost(Duration.ofSeconds(5)).untilAsserted {
            val after = repo.findById(id).get()
            assertEquals("Updated Product", after.name)
            assertEquals(299.99, after.price)
            assertEquals(2L, after.version)
            // createdAt preserved
            assertEquals(beforeCreatedAt, after.createdAt)
        }
    }

    @Test
    fun `should delete existing product`() {
        val id = "test-delete-002"
        // Seed create
        input.send(GenericMessage(createProductChange(productId = id, changeType = ChangeType.CREATED, version = 1L)))
        await().atMost(Duration.ofSeconds(5)).untilAsserted { assertTrue(repo.existsById(id)) }

        // Send delete
        input.send(GenericMessage(createProductChange(productId = id, changeType = ChangeType.DELETED)))

        await().atMost(Duration.ofSeconds(5)).untilAsserted {
            assertTrue(!repo.existsById(id))
        }
    }
}
