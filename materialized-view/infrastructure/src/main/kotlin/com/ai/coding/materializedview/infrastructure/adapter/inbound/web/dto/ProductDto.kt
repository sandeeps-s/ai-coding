package com.ai.coding.materializedview.infrastructure.adapter.inbound.web.dto

import com.ai.coding.materializedview.domain.model.Product
import java.math.BigDecimal
import java.time.Instant

/**
 * Response DTO for web API
 * This is the external representation of a product
 */
data class ProductResponse(
    val productId: String,
    val name: String,
    val description: String?,
    val price: BigDecimal?,
    val category: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long
) {
    companion object {
        fun fromDomain(product: Product): ProductResponse = ProductResponse(
            productId = product.productId,
            name = product.name,
            description = product.description,
            price = product.price,
            category = product.category,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt,
            version = product.version
        )
    }
}

/**
 * Request DTO for creating/updating products
 */
data class ProductRequest(
    val name: String,
    val description: String?,
    val price: BigDecimal?,
    val category: String?,
    val version: Long = 1L
) {
    fun toDomain(productId: String): Product = Product.create(
        productId = productId,
        name = name,
        description = description,
        price = price,
        category = category,
        version = version
    )
}
