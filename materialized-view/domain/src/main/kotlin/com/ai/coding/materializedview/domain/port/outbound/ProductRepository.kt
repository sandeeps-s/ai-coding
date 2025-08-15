package com.ai.coding.materializedview.domain.port.outbound

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId

/**
 * Outbound port for product persistence operations
 * This interface defines how the domain layer interacts with persistence
 */
interface ProductRepository {
    fun findById(productId: ProductId): Product?
    fun findAll(): List<Product>
    fun findByCategory(category: String): List<Product>
    fun findByPriceBetween(minPrice: Price, maxPrice: Price): List<Product>
    fun findByCategoryAndPriceBetween(category: String, minPrice: Price, maxPrice: Price): List<Product>
    fun save(product: Product): Product
    fun deleteById(productId: ProductId)
    fun existsById(productId: ProductId): Boolean
}
