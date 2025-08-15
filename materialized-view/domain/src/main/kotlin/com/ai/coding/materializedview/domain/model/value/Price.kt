package com.ai.coding.materializedview.domain.model.value

import java.math.BigDecimal

@JvmInline
value class Price(val value: BigDecimal) {
    init {
        require(value >= BigDecimal.ZERO) { "Price must be non-negative" }
    }
    override fun toString(): String = value.toPlainString()
    companion object {
        fun of(value: BigDecimal) = Price(value)
        fun of(value: Double) = Price(BigDecimal.valueOf(value))
        fun of(value: String) = Price(BigDecimal(value))
    }
}

