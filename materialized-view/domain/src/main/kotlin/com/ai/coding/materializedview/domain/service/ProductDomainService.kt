package com.ai.coding.materializedview.domain.service

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository
import java.math.BigDecimal

/**
 * Domain service implementing product use cases
 * This is the core business logic layer - NO Spring dependencies
 */
class ProductDomainService(
    private val productRepository: ProductRepository
) : ProductQueryUseCase, ProductCommandUseCase {

    // Query use cases
    override fun getProductById(productId: String): Product? {
        return productRepository.findById(productId)
    }

    override fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    override fun getProductsByCategory(category: String): List<Product> {
        return productRepository.findByCategory(category)
    }

    override fun getProductsByPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product> {
        validatePriceRange(minPrice, maxPrice)
        return productRepository.findByPriceBetween(minPrice, maxPrice)
    }

    override fun getProductsByCategoryAndPriceRange(
        category: String,
        minPrice: BigDecimal,
        maxPrice: BigDecimal
    ): List<Product> {
        validatePriceRange(minPrice, maxPrice)
        return productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice)
    }

    // Command use cases
    override fun createProduct(product: Product): Product {
        if (productRepository.existsById(product.productId)) {
            throw IllegalArgumentException("Product with ID ${product.productId} already exists")
        }
        return productRepository.save(product)
    }

    override fun updateProduct(product: Product): Product {
        val existingProduct = productRepository.findById(product.productId)
            ?: throw IllegalArgumentException("Product with ID ${product.productId} not found")

        // Business rule: version must be higher for updates
        if (product.version <= existingProduct.version) {
            throw IllegalArgumentException("Product version must be higher than existing version")
        }

        return productRepository.save(product)
    }

    override fun deleteProduct(productId: String) {
        if (!productRepository.existsById(productId)) {
            throw IllegalArgumentException("Product with ID $productId not found")
        }
        productRepository.deleteById(productId)
    }

    private fun validatePriceRange(minPrice: BigDecimal, maxPrice: BigDecimal) {
        if (minPrice < BigDecimal.ZERO) {
            throw IllegalArgumentException("Minimum price cannot be negative")
        }
        if (maxPrice < minPrice) {
            throw IllegalArgumentException("Maximum price must be greater than or equal to minimum price")
        }
    }
}
