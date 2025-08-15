package com.ai.coding.materializedview.domain.port.inbound

import com.ai.coding.materializedview.domain.model.Product
import java.math.BigDecimal

/**
 * Inbound port for product query operations
 * This interface defines the use cases for reading product data
 */
interface ProductQueryUseCase {
    fun getProductById(productId: String): Product?
    fun getAllProducts(): List<Product>
    fun getProductsByCategory(category: String): List<Product>
    fun getProductsByPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product>
    fun getProductsByCategoryAndPriceRange(category: String, minPrice: BigDecimal, maxPrice: BigDecimal): List<Product>
}

/**
 * Inbound port for product command operations
 * This interface defines the use cases for modifying product data
 */
interface ProductCommandUseCase {
    fun createProduct(product: Product): Product
    fun updateProduct(product: Product): Product
    fun deleteProduct(productId: String)
}
