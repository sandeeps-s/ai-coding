package com.ai.coding.materializedview.domain.model.value

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ProductNameTest {

    @Test
    fun `should create ProductName for non-blank value`() {
        val name = ProductName.of("Laptop")
        assertEquals("Laptop", name.value)
    }

    @Test
    fun `should fail for blank name`() {
        assertThrows(IllegalArgumentException::class.java) { ProductName.of("") }
        assertThrows(IllegalArgumentException::class.java) { ProductName.of("   ") }
    }
}

