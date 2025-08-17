package com.ai.coding.materializedview.integration

import com.ai.coding.materializedview.MaterializedViewApplication
import com.ai.coding.materializedview.avro.ChangeType
import com.ai.coding.materializedview.avro.ProductChange
import com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.repository.ProductViewRepository
import io.micrometer.core.instrument.MeterRegistry
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
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
class ProductChangeErrorHandlingTest {

    @Autowired
    private lateinit var input: InputDestination

    @Autowired
    private lateinit var meterRegistry: MeterRegistry

    @Autowired
    private lateinit var repo: ProductViewRepository

    private fun updateForMissingId(id: String): ProductChange = ProductChange.newBuilder()
        .setProductId(id)
        .setName("Doesn't matter")
        .setDescription(null)
        .setPrice(10.0)
        .setCategory("Test")
        .setChangeType(ChangeType.UPDATED)
        .setTimestamp(Instant.now().toEpochMilli())
        .setVersion(2L)
        .build()

    private fun deleteForMissingId(id: String): ProductChange = ProductChange.newBuilder()
        .setProductId(id)
        .setName("")
        .setDescription(null)
        .setPrice(0.0)
        .setCategory("")
        .setChangeType(ChangeType.DELETED)
        .setTimestamp(Instant.now().toEpochMilli())
        .setVersion(1L)
        .build()

    @Test
    fun `should record failure metrics and not persist on not-found update`() {
        val id = "missing-001"
        val before = meterRegistry.find("product.change.failures")
            .tag("changeType", "UPDATED")
            .tag("exception", "InvalidMessageException")
            .counter()?.count() ?: 0.0

        input.send(GenericMessage(updateForMissingId(id)))

        await().atMost(Duration.ofSeconds(5)).untilAsserted {
            val after = meterRegistry.find("product.change.failures")
                .tag("changeType", "UPDATED")
                .tag("exception", "InvalidMessageException")
                .counter()?.count() ?: 0.0
            assertFalse(after < before + 1.0, "Failure counter should increment")
            assertFalse(repo.existsById(id))
        }
    }

    @Test
    fun `should record failure metrics and not persist on not-found delete`() {
        val id = "missing-002"
        val before = meterRegistry.find("product.change.failures")
            .tag("changeType", "DELETED")
            .tag("exception", "IllegalArgumentException")
            .counter()?.count() ?: 0.0

        input.send(GenericMessage(deleteForMissingId(id)))

        await().atMost(Duration.ofSeconds(5)).untilAsserted {
            val after = meterRegistry.find("product.change.failures")
                .tag("changeType", "DELETED")
                .tag("exception", "IllegalArgumentException")
                .counter()?.count() ?: 0.0
            assertFalse(after < before + 1.0, "Failure counter should increment")
            assertFalse(repo.existsById(id))
        }
    }
}
