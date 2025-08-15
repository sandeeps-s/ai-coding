package com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.entity

import com.ai.coding.materializedview.domain.model.Product
import org.springframework.data.aerospike.mapping.Document
import org.springframework.data.annotation.Id
import org.springframework.data.aerospike.mapping.Field
import java.time.Instant
import java.math.BigDecimal

/**
 * Aerospike entity for product persistence
 * This belongs in infrastructure layer as it contains persistence-specific annotations
 */
@Document(collection = "product_views")
data class ProductView(
    @Id
    val productId: String,

    @Field
    val name: String,

    @Field
    val description: String?,

    @Field
    val price: Double?,

    @Field
    val category: String?,

    @Field
    val createdAt: Instant,

    @Field
    val updatedAt: Instant,

    @Field
    val version: Long = 1
) {
    companion object {
        fun fromDomain(product: Product): ProductView = ProductView(
            productId = product.productId,
            name = product.name,
            description = product.description,
            price = product.price?.toDouble(),
            category = product.category,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt,
            version = product.version
        )
    }

    fun toDomain(): Product = Product(
        productId = this.productId,
        name = this.name,
        description = this.description,
        price = this.price?.let { BigDecimal.valueOf(it) },
        category = this.category,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        version = this.version
    )
}
