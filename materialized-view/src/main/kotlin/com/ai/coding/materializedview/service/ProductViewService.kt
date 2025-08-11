package com.ai.coding.materializedview.service

import com.ai.coding.materializedview.entity.ProductView
import com.ai.coding.materializedview.repository.ProductViewRepository
import com.ai.coding.materializedview.avro.ProductChange
import com.ai.coding.materializedview.avro.ChangeType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ProductViewService(
    private val productViewRepository: ProductViewRepository
) {

    private val logger = LoggerFactory.getLogger(ProductViewService::class.java)

    fun handleProductChange(productChange: ProductChange) {
        when (productChange.changeType) {
            ChangeType.CREATED -> createProductView(productChange)
            ChangeType.UPDATED -> updateProductView(productChange)
            ChangeType.DELETED -> deleteProductView(productChange.productId)
        }
    }

    private fun createProductView(productChange: ProductChange) {
        logger.info("Creating materialized view for product: {}", productChange.productId)

        val productView = ProductView(
            productId = productChange.productId,
            name = productChange.name,
            description = productChange.description,
            price = productChange.price,
            category = productChange.category,
            createdAt = Instant.ofEpochMilli(productChange.timestamp),
            updatedAt = Instant.ofEpochMilli(productChange.timestamp),
            version = 1
        )

        productViewRepository.save(productView)
        logger.debug("Successfully created materialized view for product: {}", productChange.productId)
    }

    private fun updateProductView(productChange: ProductChange) {
        logger.info("Updating materialized view for product: {}", productChange.productId)

        val existingView = productViewRepository.findById(productChange.productId)

        if (existingView.isPresent) {
            val updatedView = existingView.get().copy(
                name = productChange.name,
                description = productChange.description,
                price = productChange.price,
                category = productChange.category,
                updatedAt = Instant.ofEpochMilli(productChange.timestamp),
                version = existingView.get().version + 1
            )

            productViewRepository.save(updatedView)
            logger.debug("Successfully updated materialized view for product: {}", productChange.productId)
        } else {
            logger.warn("Product view not found for update: {}", productChange.productId)
            // Handle as creation if not found
            createProductView(productChange)
        }
    }

    private fun deleteProductView(productId: String) {
        logger.info("Deleting materialized view for product: {}", productId)

        if (productViewRepository.existsById(productId)) {
            productViewRepository.deleteById(productId)
            logger.debug("Successfully deleted materialized view for product: {}", productId)
        } else {
            logger.warn("Product view not found for deletion: {}", productId)
        }
    }

    // Query methods for accessing materialized views
    fun getProductView(productId: String): ProductView? {
        return productViewRepository.findById(productId).orElse(null)
    }

    fun getProductsByCategory(category: String): List<ProductView> {
        return productViewRepository.findByCategory(category)
    }

    fun getProductsByPriceRange(minPrice: Double, maxPrice: Double): List<ProductView> {
        return productViewRepository.findByPriceBetween(minPrice, maxPrice)
    }

    fun getProductsByCategoryAndPriceRange(
        category: String,
        minPrice: Double,
        maxPrice: Double
    ): List<ProductView> {
        return productViewRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice)
    }
}
