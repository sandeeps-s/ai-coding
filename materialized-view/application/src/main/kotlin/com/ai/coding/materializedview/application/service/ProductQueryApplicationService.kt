package com.ai.coding.materializedview.application.service

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import java.math.BigDecimal
import java.util.*

/**
 * Application service orchestrating product query use cases.
 * Delegates business rules to the domain layer.
 */
class ProductQueryApplicationService(
    private val domainQueries: ProductQueryUseCase
) : ProductQueryUseCase {

    override fun getProductById(productId: String): Product? =
        domainQueries.getProductById(productId)

    override fun getAllProducts(): List<Product> =
        domainQueries.getAllProducts()

    override fun getProductsByCategory(category: String): List<Product> =
        domainQueries.getProductsByCategory(category)

    override fun getProductsByPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product> =
        domainQueries.getProductsByPriceRange(minPrice, maxPrice)

    override fun getProductsByCategoryAndPriceRange(
        category: String,
        minPrice: BigDecimal,
        maxPrice: BigDecimal
    ): List<Product> =
        domainQueries.getProductsByCategoryAndPriceRange(category, minPrice, maxPrice)
}
