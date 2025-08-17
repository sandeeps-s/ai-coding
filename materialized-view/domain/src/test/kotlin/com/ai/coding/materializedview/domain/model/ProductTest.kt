package com.ai.coding.materializedview.domain.model

import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Instant

class ProductTest {

    @Test
    fun `create should initialize timestamps and accept version at least 1`() {
        val p = Product.create(
            productId = ProductId.of("p-1"),
            name = ProductName.of("Name"),
            description = "desc",
            price = Price.of(10.0),
            category = "cat",
            version = 2
        )
        assertEquals("p-1", p.productId.value)
        assertEquals("Name", p.name.value)
        assertEquals(2, p.version)
    }

    @Test
    fun `create should fail for version less than 1`() {
        assertThrows(IllegalArgumentException::class.java) {
            Product.create(ProductId.of("p-2"), ProductName.of("N"), version = 0)
        }
    }

    @Test
    fun `update should allow same or higher version`() {
        val p = Product.create(ProductId.of("p-3"), ProductName.of("N"), version = 1)
        val u1 = p.update(version = 1)
        val u2 = p.update(version = 2)
        assertEquals(1, u1.version)
        assertEquals(2, u2.version)
    }

    @Test
    fun `update should fail for lower version`() {
        val p = Product.create(ProductId.of("p-4"), ProductName.of("N"), version = 2)
        assertThrows(IllegalArgumentException::class.java) {
            p.update(version = 1)
        }
    }
}
