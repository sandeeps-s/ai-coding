package com.ai.coding.materializedview.domain.port.outbound

import com.ai.coding.materializedview.domain.model.Product
import java.util.*

/**
 * Outbound port for product persistence operations
 * This interface defines how the domain layer interacts with persistence
 */
interface ProductRepository {
    fun findById(productId: String): Optional<Product>
    fun findAll(): List<Product>
    fun findByCategory(category: String): List<Product>
    fun findByPriceBetween(minPrice: Double, maxPrice: Double): List<Product>
    fun findByCategoryAndPriceBetween(category: String, minPrice: Double, maxPrice: Double): List<Product>
    fun save(product: Product): Product
    fun deleteById(productId: String)
    fun existsById(productId: String): Boolean
}
