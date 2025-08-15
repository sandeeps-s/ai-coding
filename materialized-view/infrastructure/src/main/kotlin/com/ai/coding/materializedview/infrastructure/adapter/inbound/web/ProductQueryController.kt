package com.ai.coding.materializedview.infrastructure.adapter.inbound.web

import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.infrastructure.adapter.inbound.web.dto.ProductResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

/**
 * Web adapter for product queries (inbound adapter)
 * Now uses application services from the application module
 */
@RestController
@RequestMapping("/api/products")
class ProductQueryController(
    private val productQueryUseCase: ProductQueryUseCase
) {

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: String): ResponseEntity<ProductResponse> {
        val product = productQueryUseCase.getProductById(ProductId.of(productId))
        return if (product != null) {
            ResponseEntity.ok(ProductResponse.fromDomain(product))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAllProducts(): List<ProductResponse> {
        return productQueryUseCase.getAllProducts()
            .map { ProductResponse.fromDomain(it) }
    }

    @GetMapping("/category/{category}")
    fun getProductsByCategory(@PathVariable category: String): List<ProductResponse> {
        return productQueryUseCase.getProductsByCategory(category)
            .map { ProductResponse.fromDomain(it) }
    }

    @GetMapping("/price-range")
    fun getProductsByPriceRange(
        @RequestParam minPrice: BigDecimal,
        @RequestParam maxPrice: BigDecimal
    ): List<ProductResponse> {
        return productQueryUseCase.getProductsByPriceRange(Price.of(minPrice), Price.of(maxPrice))
            .map { ProductResponse.fromDomain(it) }
    }

    @GetMapping("/category/{category}/price-range")
    fun getProductsByCategoryAndPriceRange(
        @PathVariable category: String,
        @RequestParam minPrice: BigDecimal,
        @RequestParam maxPrice: BigDecimal
    ): List<ProductResponse> {
        return productQueryUseCase.getProductsByCategoryAndPriceRange(category, Price.of(minPrice), Price.of(maxPrice))
            .map { ProductResponse.fromDomain(it) }
    }
}
