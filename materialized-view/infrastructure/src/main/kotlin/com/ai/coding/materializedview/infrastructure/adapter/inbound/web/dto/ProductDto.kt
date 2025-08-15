package com.ai.coding.materializedview.infrastructure.adapter.inbound.web.dto

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
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
            productId = product.productId.value,
            name = product.name.value,
            description = product.description,
            price = product.price?.value,
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
        productId = ProductId.of(productId),
        name = ProductName.of(name),
        description = description,
        price = price?.let { Price.of(it) },
        category = category,
        version = version
    )
}
