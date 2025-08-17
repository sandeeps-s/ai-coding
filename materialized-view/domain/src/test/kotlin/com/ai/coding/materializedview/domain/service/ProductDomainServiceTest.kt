package com.ai.coding.materializedview.domain.service

import com.ai.coding.materializedview.domain.event.DomainEvent
import com.ai.coding.materializedview.domain.event.DomainEventPublisher
import com.ai.coding.materializedview.domain.event.ProductCreated
import com.ai.coding.materializedview.domain.event.ProductDeleted
import com.ai.coding.materializedview.domain.event.ProductUpdated
import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository
import io.mockk.CapturingSlot
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProductDomainServiceTest {

    @MockK(relaxed = true)
    lateinit var repo: ProductRepository

    @MockK(relaxed = true)
    lateinit var publisher: DomainEventPublisher

    private lateinit var service: ProductDomainService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        service = ProductDomainService(repo, publisher)
    }

    private fun product(id: String = "p-1", version: Long = 1) = Product.create(
        productId = ProductId.of(id),
        name = ProductName.of("Name"),
        price = Price.of(BigDecimal("10.00")),
        version = version
    )

    @Test
    fun `createProduct should save when not exists and publish event`() {
        val p = product("p-1", 1)
        every { repo.existsById(p.productId) } returns false
        every { repo.save(p) } returns p
        val eventSlot: CapturingSlot<DomainEvent> = slot()
        every { publisher.publish(capture(eventSlot)) } just runs

        service.createProduct(p)

        verify { repo.existsById(p.productId) }
        verify { repo.save(p) }
        verify { publisher.publish(any()) }
        assert(eventSlot.captured is ProductCreated)
    }

    @Test
    fun `createProduct should throw when already exists`() {
        val p = product("p-1", 1)
        every { repo.existsById(p.productId) } returns true
        assertThrows(IllegalArgumentException::class.java) { service.createProduct(p) }
        verify(exactly = 0) { repo.save(any()) }
    }

    @Test
    fun `updateProduct should require existing and higher version and publish`() {
        val existing = product("p-2", 1)
        val updated = existing.copy(version = 2)
        every { repo.findById(existing.productId) } returns existing
        every { repo.save(updated) } returns updated
        val ev: CapturingSlot<DomainEvent> = slot()
        every { publisher.publish(capture(ev)) } just runs

        service.updateProduct(updated)

        verify { repo.findById(existing.productId) }
        verify { repo.save(updated) }
        verify { publisher.publish(any()) }
        assert(ev.captured is ProductUpdated)
    }

    @Test
    fun `updateProduct should throw when not found`() {
        val p = product("missing", 2)
        every { repo.findById(p.productId) } returns null
        assertThrows(IllegalArgumentException::class.java) { service.updateProduct(p) }
    }

    @Test
    fun `updateProduct should throw when version not higher`() {
        val existing = product("p-3", 2)
        val notHigher = existing.copy(version = 2)
        every { repo.findById(existing.productId) } returns existing
        assertThrows(IllegalArgumentException::class.java) { service.updateProduct(notHigher) }
    }

    @Test
    fun `deleteProduct should remove when exists and publish`() {
        val id = ProductId.of("p-4")
        every { repo.existsById(id) } returns true
        every { repo.deleteById(id) } just runs
        val ev: CapturingSlot<DomainEvent> = slot()
        every { publisher.publish(capture(ev)) } just runs

        service.deleteProduct(id)

        verify { repo.existsById(id) }
        verify { repo.deleteById(id) }
        verify { publisher.publish(any()) }
        assert(ev.captured is ProductDeleted)
    }

    @Test
    fun `deleteProduct should throw when missing`() {
        val id = ProductId.of("missing")
        every { repo.existsById(id) } returns false
        assertThrows(IllegalArgumentException::class.java) { service.deleteProduct(id) }
    }

    @Test
    fun `getProductsByPriceRange should validate range`() {
        val min = Price.of(BigDecimal("10"))
        val maxBad = Price.of(BigDecimal("5"))
        assertThrows(IllegalArgumentException::class.java) { service.getProductsByPriceRange(min, maxBad) }
    }
}
