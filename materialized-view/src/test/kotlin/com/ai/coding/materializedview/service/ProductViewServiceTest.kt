package com.ai.coding.materializedview.service

import com.ai.coding.materializedview.avro.ProductChange
import com.ai.coding.materializedview.avro.ChangeType
import com.ai.coding.materializedview.entity.ProductView
import com.ai.coding.materializedview.repository.ProductViewRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.slot
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import java.time.Instant
import java.util.Optional

class ProductViewServiceTest {

    private lateinit var productViewRepository: ProductViewRepository
    private lateinit var productViewService: ProductViewService

    @BeforeEach
    fun setup() {
        productViewRepository = mockk(relaxed = true)
        productViewService = ProductViewService(productViewRepository)
    }

    @Test
    fun `should create materialized view for product creation event`() {
        // Given
        val productChange = ProductChange.newBuilder()
            .setProductId("product-123")
            .setName("Test Product")
            .setDescription("Test Description")
            .setPrice(99.99)
            .setCategory("Electronics")
            .setChangeType(ChangeType.CREATED)
            .setTimestamp(System.currentTimeMillis())
            .setVersion(1L)
            .build()

        // Mock the save method to return the same object that was passed to it
        every { productViewRepository.save(any<ProductView>()) } answers { firstArg() }

        // When
        productViewService.handleProductChange(productChange)

        // Then
        verify {
            productViewRepository.save(any<ProductView>())
        }
    }

    @Test
    fun `should update materialized view for product update event`() {
        // Given
        val existingView = ProductView(
            productId = "product-123",
            name = "Old Product",
            description = "Old Description",
            price = 50.0,
            category = "Electronics",
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            version = 1L
        )

        every { productViewRepository.findById("product-123") } returns Optional.of(existingView)
        every { productViewRepository.save(any<ProductView>()) } answers { firstArg() }

        val productChange = ProductChange.newBuilder()
            .setProductId("product-123")
            .setName("Updated Product")
            .setDescription("Updated Description")
            .setPrice(199.99)
            .setCategory("Electronics")
            .setChangeType(ChangeType.UPDATED)
            .setTimestamp(System.currentTimeMillis())
            .setVersion(2L)
            .build()

        // When
        productViewService.handleProductChange(productChange)

        // Then
        verify {
            productViewRepository.save(match<ProductView> {
                it.name == "Updated Product" &&
                it.price == 199.99 &&
                it.version == 2L
            })
        }
    }

    @Test
    fun `should delete materialized view for product deletion event`() {
        // Given
        every { productViewRepository.existsById("product-123") } returns true

        val productChange = ProductChange.newBuilder()
            .setProductId("product-123")
            .setName("Test Product")
            .setDescription("Test Description")
            .setPrice(99.99)
            .setCategory("Electronics")
            .setChangeType(ChangeType.DELETED)
            .setTimestamp(System.currentTimeMillis())
            .setVersion(1L)
            .build()

        // When
        productViewService.handleProductChange(productChange)

        // Then
        verify {
            productViewRepository.deleteById("product-123")
        }
    }
}
