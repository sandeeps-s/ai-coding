package com.ai.coding.materializedview.domain.model.value

@JvmInline
value class ProductName(val value: String) {
    init {
        require(value.isNotBlank()) { "Product name must not be blank" }
    }
    override fun toString(): String = value
    companion object {
        fun of(value: String) = ProductName(value)
    }
}

