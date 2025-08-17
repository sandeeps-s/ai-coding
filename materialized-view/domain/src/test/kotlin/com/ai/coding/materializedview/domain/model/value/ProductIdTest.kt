package com.ai.coding.materializedview.domain.model.value

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ProductIdTest {

    @Test
    fun `should create ProductId for non-blank value`() {
        val id = ProductId.of("ABC-123")
        assertEquals("ABC-123", id.value)
    }

    @Test
    fun `should fail for blank value`() {
        assertThrows(IllegalArgumentException::class.java) { ProductId.of("") }
        assertThrows(IllegalArgumentException::class.java) { ProductId.of("   ") }
    }
}

