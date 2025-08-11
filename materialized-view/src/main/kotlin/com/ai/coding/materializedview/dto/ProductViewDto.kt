package com.ai.coding.materializedview.dto

import java.time.Instant

data class ProductViewResponse(
    val productId: String,
    val name: String,
    val description: String?,
    val price: Double?,
    val category: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long
)

data class ProductSearchCriteria(
    val category: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null
)
