package com.ai.coding.materializedview.domain.model.value

@JvmInline
value class ProductId(val value: String) {
    init {
        require(value.isNotBlank()) { "ProductId must not be blank" }
    }
    override fun toString(): String = value
    companion object {
        fun of(value: String) = ProductId(value)
    }
}

