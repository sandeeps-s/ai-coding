package com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.entity

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
import org.springframework.data.aerospike.mapping.Document
import org.springframework.data.annotation.Id
import org.springframework.data.aerospike.mapping.Field
import java.time.Instant

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
            productId = product.productId.value,
            name = product.name.value,
            description = product.description,
            price = product.price?.value?.toDouble(),
            category = product.category,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt,
            version = product.version
        )
    }

    fun toDomain(): Product = Product(
        productId = ProductId.of(this.productId),
        name = ProductName.of(this.name),
        description = this.description,
        price = this.price?.let { Price.of(it) },
        category = this.category,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        version = this.version
    )
}
