package com.ai.coding.materializedview.domain.model

import java.time.Instant

/**
 * Product domain entity - core business object
 */
data class Product(
    val productId: String,
    val name: String,
    val description: String?,
    val price: Double?,
    val category: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long
) {
    companion object {
        fun create(
            productId: String,
            name: String,
            description: String? = null,
            price: Double? = null,
            category: String? = null,
            version: Long = 1L
        ): Product {
            val now = Instant.now()
            return Product(
                productId = productId,
                name = name,
                description = description,
                price = price,
                category = category,
                createdAt = now,
                updatedAt = now,
                version = version
            )
        }
    }

    fun update(
        name: String? = null,
        description: String? = null,
        price: Double? = null,
        category: String? = null,
        version: Long? = null
    ): Product {
        return this.copy(
            name = name ?: this.name,
            description = description ?: this.description,
            price = price ?: this.price,
            category = category ?: this.category,
            updatedAt = Instant.now(),
            version = version ?: this.version
        )
    }

    fun isInPriceRange(minPrice: Double, maxPrice: Double): Boolean {
        return price?.let { it >= minPrice && it <= maxPrice } ?: false
    }
}
