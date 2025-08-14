package com.ai.coding.materializedview.application.service

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository

/**
 * Application service implementing product command use cases
 * This layer handles business rules and coordinates with repositories
 */
class ProductCommandApplicationService(
    private val productRepository: ProductRepository
) : ProductCommandUseCase {

    override fun createProduct(product: Product): Product {
        if (productRepository.existsById(product.productId)) {
            throw IllegalArgumentException("Product with ID ${product.productId} already exists")
        }
        return productRepository.save(product)
    }

    override fun updateProduct(product: Product): Product {
        val existingProduct = productRepository.findById(product.productId)
            .orElseThrow { IllegalArgumentException("Product with ID ${product.productId} not found") }

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
}
