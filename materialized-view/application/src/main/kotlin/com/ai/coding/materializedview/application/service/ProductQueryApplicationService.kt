package com.ai.coding.materializedview.application.service

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * Application service implementing product query use cases
 * This layer orchestrates domain operations and coordinates with repositories
 */
@Service
class ProductQueryApplicationService(
    private val productRepository: ProductRepository
) : ProductQueryUseCase {

    override fun getProductById(productId: String): Optional<Product> {
        return productRepository.findById(productId)
    }

    override fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    override fun getProductsByCategory(category: String): List<Product> {
        return productRepository.findByCategory(category)
    }

    override fun getProductsByPriceRange(minPrice: Double, maxPrice: Double): List<Product> {
        validatePriceRange(minPrice, maxPrice)
        return productRepository.findByPriceBetween(minPrice, maxPrice)
    }

    override fun getProductsByCategoryAndPriceRange(
        category: String,
        minPrice: Double,
        maxPrice: Double
    ): List<Product> {
        validatePriceRange(minPrice, maxPrice)
        return productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice)
    }

    private fun validatePriceRange(minPrice: Double, maxPrice: Double) {
        if (minPrice < 0) {
            throw IllegalArgumentException("Minimum price cannot be negative")
        }
        if (maxPrice < minPrice) {
            throw IllegalArgumentException("Maximum price must be greater than or equal to minimum price")
        }
    }
}
