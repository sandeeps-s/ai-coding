package com.ai.coding.materializedview.infrastructure.adapter.inbound.messaging

import com.ai.coding.materializedview.avro.ChangeType
import com.ai.coding.materializedview.avro.ProductChange
import com.ai.coding.materializedview.domain.exception.InvalidMessageException
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class ProductChangeMessageHandlerTest {

    private lateinit var productCommandUseCase: ProductCommandUseCase
    private lateinit var productQueryUseCase: ProductQueryUseCase
    private val meterRegistry = SimpleMeterRegistry()

    private lateinit var handler: ProductChangeMessageHandler

    @BeforeEach
    fun setUp() {
        productCommandUseCase = mockk(relaxed = true)
        productQueryUseCase = mockk(relaxed = true)
        handler = ProductChangeMessageHandler(productCommandUseCase, productQueryUseCase, meterRegistry)
    }

    @Test
    fun `should invoke create use case for CREATED`() {
        val record = ProductChange.newBuilder()
            .setProductId("p-1")
            .setName("Name")
            .setDescription("desc")
            .setPrice(10.0)
            .setCategory("cat")
            .setChangeType(ChangeType.CREATED)
            .setTimestamp(Instant.now().toEpochMilli())
            .setVersion(1L)
            .build()

        every { productCommandUseCase.createProduct(any()) } answers { firstArg() }

        handler.handleMessage(record)

        verify(exactly = 1) { productCommandUseCase.createProduct(any()) }
    }

    @Test
    fun `should throw InvalidMessageException when productId is blank`() {
        val record = ProductChange.newBuilder()
            .setProductId("   ")
            .setName("Name")
            .setDescription("desc")
            .setPrice(10.0)
            .setCategory("cat")
            .setChangeType(ChangeType.CREATED)
            .setTimestamp(Instant.now().toEpochMilli())
            .setVersion(1L)
            .build()

        assertThrows(InvalidMessageException::class.java) {
            handler.handleMessage(record)
        }
    }

    @Test
    fun `should throw InvalidMessageException when name is blank`() {
        val record = ProductChange.newBuilder()
            .setProductId("p-1")
            .setName("   ")
            .setDescription("desc")
            .setPrice(10.0)
            .setCategory("cat")
            .setChangeType(ChangeType.CREATED)
            .setTimestamp(Instant.now().toEpochMilli())
            .setVersion(1L)
            .build()

        assertThrows(InvalidMessageException::class.java) {
            handler.handleMessage(record)
        }
    }

    @Test
    fun `should throw InvalidMessageException for update when product not found`() {
        val id = ProductId.of("missing")
        every { productQueryUseCase.getProductById(id) } returns null

        val record = ProductChange.newBuilder()
            .setProductId(id.value)
            .setName("Name")
            .setDescription("desc")
            .setPrice(10.0)
            .setCategory("cat")
            .setChangeType(ChangeType.UPDATED)
            .setTimestamp(Instant.now().toEpochMilli())
            .setVersion(2L)
            .build()

        assertThrows(InvalidMessageException::class.java) {
            handler.handleMessage(record)
        }
    }

    @Test
    fun `should propagate IllegalArgumentException when price negative`() {
        val record = ProductChange.newBuilder()
            .setProductId("p-1")
            .setName("Name")
            .setDescription("desc")
            .setPrice(-5.0) // invalid
            .setCategory("cat")
            .setChangeType(ChangeType.CREATED)
            .setTimestamp(Instant.now().toEpochMilli())
            .setVersion(1L)
            .build()

        assertThrows(IllegalArgumentException::class.java) {
            handler.handleMessage(record)
        }
    }
}
