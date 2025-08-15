package com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository
import com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.entity.ProductView
import com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.repository.ProductViewRepository
import org.springframework.stereotype.Repository

/**
 * Adapter that implements the ProductRepository port using Aerospike
 * This is the outbound adapter for persistence
 */
@Repository
class AerospikeProductRepositoryAdapter(
    private val productViewRepository: ProductViewRepository
) : ProductRepository {

    override fun findById(productId: ProductId): Product? {
        return productViewRepository.findById(productId.value)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAll(): List<Product> {
        return productViewRepository.findAll()
            .map { it.toDomain() }
    }

    override fun findByCategory(category: String): List<Product> {
        return productViewRepository.findByCategory(category)
            .map { it.toDomain() }
    }

    override fun findByPriceBetween(minPrice: Price, maxPrice: Price): List<Product> {
        return productViewRepository.findByPriceBetween(minPrice.value.toDouble(), maxPrice.value.toDouble())
            .map { it.toDomain() }
    }

    override fun findByCategoryAndPriceBetween(
        category: String,
        minPrice: Price,
        maxPrice: Price
    ): List<Product> {
        return productViewRepository.findByCategoryAndPriceBetween(category, minPrice.value.toDouble(), maxPrice.value.toDouble())
            .map { it.toDomain() }
    }

    override fun save(product: Product): Product {
        val entity = ProductView.fromDomain(product)
        val saved = productViewRepository.save(entity)
        return saved.toDomain()
    }

    override fun deleteById(productId: ProductId) {
        productViewRepository.deleteById(productId.value)
    }

    override fun existsById(productId: ProductId): Boolean {
        return productViewRepository.existsById(productId.value)
    }
}
