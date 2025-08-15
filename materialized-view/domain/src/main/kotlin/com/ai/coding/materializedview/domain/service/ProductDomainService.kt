package com.ai.coding.materializedview.domain.service

import com.ai.coding.materializedview.domain.event.DomainEventPublisher
import com.ai.coding.materializedview.domain.event.ProductCreated
import com.ai.coding.materializedview.domain.event.ProductDeleted
import com.ai.coding.materializedview.domain.event.ProductUpdated
import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository
import java.math.BigDecimal

/**
 * Domain service implementing product use cases
 * This is the core business logic layer - NO Spring dependencies
 */
class ProductDomainService(
    private val productRepository: ProductRepository,
    private val eventPublisher: DomainEventPublisher
) : ProductQueryUseCase, ProductCommandUseCase {

    // Query use cases
    override fun getProductById(productId: ProductId): Product? {
        return productRepository.findById(productId)
    }

    override fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    override fun getProductsByCategory(category: String): List<Product> {
        return productRepository.findByCategory(category)
    }

    override fun getProductsByPriceRange(minPrice: Price, maxPrice: Price): List<Product> {
        validatePriceRange(minPrice, maxPrice)
        return productRepository.findByPriceBetween(minPrice, maxPrice)
    }

    override fun getProductsByCategoryAndPriceRange(
        category: String,
        minPrice: Price,
        maxPrice: Price
    ): List<Product> {
        validatePriceRange(minPrice, maxPrice)
        return productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice)
    }

    // Command use cases
    override fun createProduct(product: Product): Product {
        if (productRepository.existsById(product.productId)) {
            throw IllegalArgumentException("Product with ID ${product.productId} already exists")
        }
        val saved = productRepository.save(product)
        eventPublisher.publish(ProductCreated(saved))
        return saved
    }

    override fun updateProduct(product: Product): Product {
        val existingProduct = productRepository.findById(product.productId)
            ?: throw IllegalArgumentException("Product with ID ${product.productId} not found")

        // Business rule: version must be higher for updates
        if (product.version <= existingProduct.version) {
            throw IllegalArgumentException("Product version must be higher than existing version")
        }

        val saved = productRepository.save(product)
        eventPublisher.publish(ProductUpdated(saved))
        return saved
    }

    override fun deleteProduct(productId: ProductId) {
        if (!productRepository.existsById(productId)) {
            throw IllegalArgumentException("Product with ID $productId not found")
        }
        productRepository.deleteById(productId)
        eventPublisher.publish(ProductDeleted(productId))
    }

    private fun validatePriceRange(minPrice: Price, maxPrice: Price) {
        if (minPrice.value < BigDecimal.ZERO) {
            throw IllegalArgumentException("Minimum price cannot be negative")
        }
        if (maxPrice.value < minPrice.value) {
            throw IllegalArgumentException("Maximum price must be greater than or equal to minimum price")
        }
    }
}
