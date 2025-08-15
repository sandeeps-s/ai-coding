package com.ai.coding.materializedview.domain.port.inbound

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId

/**
 * Inbound port for product query operations
 * This interface defines the use cases for reading product data
 */
interface ProductQueryUseCase {
    fun getProductById(productId: ProductId): Product?
    fun getAllProducts(): List<Product>
    fun getProductsByCategory(category: String): List<Product>
    fun getProductsByPriceRange(minPrice: Price, maxPrice: Price): List<Product>
    fun getProductsByCategoryAndPriceRange(category: String, minPrice: Price, maxPrice: Price): List<Product>
}

/**
 * Inbound port for product command operations
 * This interface defines the use cases for modifying product data
 */
interface ProductCommandUseCase {
    fun createProduct(product: Product): Product
    fun updateProduct(product: Product): Product
    fun deleteProduct(productId: ProductId)
}
