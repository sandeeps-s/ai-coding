package com.ai.coding.materializedview.controller

import com.ai.coding.materializedview.dto.ProductViewResponse
import com.ai.coding.materializedview.service.ProductQueryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductViewController(private val productQueryService: ProductQueryService) {

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: String): ResponseEntity<ProductViewResponse> {
        val product = productQueryService.findProductById(productId)
        return if (product.isPresent) {
            ResponseEntity.ok(product.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAllProducts(): List<ProductViewResponse> {
        return productQueryService.findAllProducts()
    }

    @GetMapping("/category/{category}")
    fun getProductsByCategory(@PathVariable category: String): List<ProductViewResponse> {
        return productQueryService.findProductsByCategory(category)
    }

    @GetMapping("/price-range")
    fun getProductsByPriceRange(
        @RequestParam minPrice: Double,
        @RequestParam maxPrice: Double
    ): List<ProductViewResponse> {
        return productQueryService.findProductsByPriceRange(minPrice, maxPrice)
    }

    @GetMapping("/category/{category}/price-range")
    fun getProductsByCategoryAndPriceRange(
        @PathVariable category: String,
        @RequestParam minPrice: Double,
        @RequestParam maxPrice: Double
    ): List<ProductViewResponse> {
        return productQueryService.findProductsByCategoryAndPriceRange(category, minPrice, maxPrice)
    }
}
