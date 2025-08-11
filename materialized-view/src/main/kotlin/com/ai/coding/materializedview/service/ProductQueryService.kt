package com.ai.coding.materializedview.service

import com.ai.coding.materializedview.dto.ProductViewResponse
import com.ai.coding.materializedview.entity.ProductView
import com.ai.coding.materializedview.repository.ProductViewRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProductQueryService(private val productViewRepository: ProductViewRepository) {

    fun findProductById(productId: String): Optional<ProductViewResponse> {
        return productViewRepository.findById(productId)
            .map { it.toResponse() }
    }

    fun findAllProducts(): List<ProductViewResponse> {
        return productViewRepository.findAll()
            .map { it.toResponse() }
    }

    fun findProductsByCategory(category: String): List<ProductViewResponse> {
        return productViewRepository.findByCategory(category)
            .map { it.toResponse() }
    }

    fun findProductsByPriceRange(minPrice: Double, maxPrice: Double): List<ProductViewResponse> {
        return productViewRepository.findByPriceBetween(minPrice, maxPrice)
            .map { it.toResponse() }
    }

    fun findProductsByCategoryAndPriceRange(
        category: String,
        minPrice: Double,
        maxPrice: Double
    ): List<ProductViewResponse> {
        return productViewRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice)
            .map { it.toResponse() }
    }

    private fun ProductView.toResponse() = ProductViewResponse(
        productId = productId,
        name = name,
        description = description,
        price = price,
        category = category,
        createdAt = createdAt,
        updatedAt = updatedAt,
        version = version
    )
}
