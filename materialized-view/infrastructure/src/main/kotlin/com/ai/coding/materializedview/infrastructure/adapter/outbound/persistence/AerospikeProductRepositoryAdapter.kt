package com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository
import com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.entity.ProductView
import com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.repository.ProductViewRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal

/**
 * Adapter that implements the ProductRepository port using Aerospike
 * This is the outbound adapter for persistence
 */
@Repository
class AerospikeProductRepositoryAdapter(
    private val productViewRepository: ProductViewRepository
) : ProductRepository {

    override fun findById(productId: String): Product? {
        return productViewRepository.findById(productId)
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

    override fun findByPriceBetween(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product> {
        return productViewRepository.findByPriceBetween(minPrice.toDouble(), maxPrice.toDouble())
            .map { it.toDomain() }
    }

    override fun findByCategoryAndPriceBetween(
        category: String,
        minPrice: BigDecimal,
        maxPrice: BigDecimal
    ): List<Product> {
        return productViewRepository.findByCategoryAndPriceBetween(category, minPrice.toDouble(), maxPrice.toDouble())
            .map { it.toDomain() }
    }

    override fun save(product: Product): Product {
        val entity = ProductView.fromDomain(product)
        val saved = productViewRepository.save(entity)
        return saved.toDomain()
    }

    override fun deleteById(productId: String) {
        productViewRepository.deleteById(productId)
    }

    override fun existsById(productId: String): Boolean {
        return productViewRepository.existsById(productId)
    }
}
