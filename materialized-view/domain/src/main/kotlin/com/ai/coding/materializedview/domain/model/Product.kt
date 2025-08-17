package com.ai.coding.materializedview.domain.model

import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
import java.time.Instant

/**
 * Product domain entity - core business object
 */
data class Product(
    val productId: ProductId,
    val name: ProductName,
    val description: String?,
    val price: Price?,
    val category: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long
) {
    companion object {
        fun create(
            productId: ProductId,
            name: ProductName,
            description: String? = null,
            price: Price? = null,
            category: String? = null,
            version: Long = 1L
        ): Product {
            val now = Instant.now()
            require(version >= 1) { "Version must be >= 1" }
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
        name: ProductName? = null,
        description: String? = null,
        price: Price? = null,
        category: String? = null,
        version: Long? = null
    ): Product {
        val newVersion = version ?: this.version
        // Versioning rule is enforced at the domain service layer; entity is permissive.
        return this.copy(
            name = name ?: this.name,
            description = description ?: this.description,
            price = price ?: this.price,
            category = category ?: this.category,
            updatedAt = Instant.now(),
            version = newVersion
        )
    }
}
