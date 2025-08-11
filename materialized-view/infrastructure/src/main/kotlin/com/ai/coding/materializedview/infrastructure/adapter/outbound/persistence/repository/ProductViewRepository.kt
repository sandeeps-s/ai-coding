package com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.repository

import com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.entity.ProductView
import org.springframework.data.aerospike.repository.AerospikeRepository
import org.springframework.stereotype.Repository

/**
 * Aerospike repository interface - infrastructure layer
 * This is the technical implementation detail for persistence
 */
@Repository
interface ProductViewRepository : AerospikeRepository<ProductView, String> {

    fun findByCategory(category: String): List<ProductView>

    fun findByPriceBetween(minPrice: Double, maxPrice: Double): List<ProductView>

    fun findByCategoryAndPriceBetween(
        category: String,
        minPrice: Double,
        maxPrice: Double
    ): List<ProductView>
}
