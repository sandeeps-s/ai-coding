package com.ai.coding.materializedview.domain.model.value

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PriceTest {

    @Test
    fun `should create price with zero or positive values`() {
        val p0 = Price.of(0.0)
        val p1 = Price.of(BigDecimal("10.50"))
        val p2 = Price.of("20.00")

        assertEquals(0, p0.value.compareTo(BigDecimal.ZERO))
        assertEquals(0, p1.value.compareTo(BigDecimal("10.50")))
        assertEquals(0, p2.value.compareTo(BigDecimal("20.00")))
    }

    @Test
    fun `should fail for negative values`() {
        assertThrows(IllegalArgumentException::class.java) { Price.of(-0.01) }
        assertThrows(IllegalArgumentException::class.java) { Price.of(BigDecimal("-1")) }
    }
}

