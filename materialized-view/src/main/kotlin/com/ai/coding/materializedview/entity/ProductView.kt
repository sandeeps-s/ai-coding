package com.ai.coding.materializedview.entity

import org.springframework.data.aerospike.mapping.Document
import org.springframework.data.annotation.Id
import org.springframework.data.aerospike.mapping.Field
import java.time.Instant

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
)
