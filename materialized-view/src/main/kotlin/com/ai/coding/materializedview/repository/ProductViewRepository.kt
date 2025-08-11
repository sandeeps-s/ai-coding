package com.ai.coding.materializedview.repository

import com.ai.coding.materializedview.entity.ProductView
import org.springframework.data.aerospike.repository.AerospikeRepository
import org.springframework.stereotype.Repository

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
