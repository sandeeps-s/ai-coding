package com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository
import com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.entity.ProductView
import com.ai.coding.materializedview.infrastructure.adapter.outbound.persistence.repository.ProductViewRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Adapter that implements the ProductRepository port using Aerospike
 * This is the outbound adapter for persistence
 */
@Repository
class AerospikeProductRepositoryAdapter(
    private val productViewRepository: ProductViewRepository
) : ProductRepository {

    override fun findById(productId: String): Optional<Product> {
        return productViewRepository.findById(productId)
            .map { it.toDomain() }
    }

    override fun findAll(): List<Product> {
        return productViewRepository.findAll()
            .map { it.toDomain() }
    }

    override fun findByCategory(category: String): List<Product> {
        return productViewRepository.findByCategory(category)
            .map { it.toDomain() }
    }

    override fun findByPriceBetween(minPrice: Double, maxPrice: Double): List<Product> {
        return productViewRepository.findByPriceBetween(minPrice, maxPrice)
            .map { it.toDomain() }
    }

    override fun findByCategoryAndPriceBetween(
        category: String,
        minPrice: Double,
        maxPrice: Double
    ): List<Product> {
        return productViewRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice)
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

// Extension functions to convert between domain and entity
private fun ProductView.toDomain(): Product = Product(
    productId = this.productId,
    name = this.name,
    description = this.description,
    price = this.price,
    category = this.category,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    version = this.version
)

private fun ProductView.Companion.fromDomain(product: Product): ProductView = ProductView(
    productId = product.productId,
    name = product.name,
    description = product.description,
    price = product.price,
    category = product.category,
    createdAt = product.createdAt,
    updatedAt = product.updatedAt,
    version = product.version
)
