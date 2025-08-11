package com.ai.coding.materializedview.infrastructure.adapter.inbound.web

import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.infrastructure.adapter.inbound.web.dto.ProductResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        val product = productQueryUseCase.getProductById(productId)
        return if (product.isPresent) {
            ResponseEntity.ok(ProductResponse.fromDomain(product.get()))
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
        @RequestParam minPrice: Double,
        @RequestParam maxPrice: Double
    ): List<ProductResponse> {
        return productQueryUseCase.getProductsByPriceRange(minPrice, maxPrice)
            .map { ProductResponse.fromDomain(it) }
    }

    @GetMapping("/category/{category}/price-range")
    fun getProductsByCategoryAndPriceRange(
        @PathVariable category: String,
        @RequestParam minPrice: Double,
        @RequestParam maxPrice: Double
    ): List<ProductResponse> {
        return productQueryUseCase.getProductsByCategoryAndPriceRange(category, minPrice, maxPrice)
            .map { ProductResponse.fromDomain(it) }
    }
}
