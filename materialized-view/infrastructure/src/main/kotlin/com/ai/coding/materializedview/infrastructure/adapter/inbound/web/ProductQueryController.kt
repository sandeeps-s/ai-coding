package com.ai.coding.materializedview.infrastructure.adapter.inbound.web

import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.infrastructure.adapter.inbound.web.dto.ProductResponse
import com.ai.coding.materializedview.infrastructure.adapter.shared.InputSanitizer
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

/**
 * Web adapter for product queries (inbound adapter)
 * Now uses application services from the application module
 */
@RestController
@RequestMapping(value = ["/api/v1/products", "/api/products"])  // v1 + legacy path
@Validated
class ProductQueryController(
    private val productQueryUseCase: ProductQueryUseCase
) {

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable @NotBlank productId: String): ResponseEntity<ProductResponse> {
        val sanitizedId = InputSanitizer.sanitizePathSegment(productId)
        val product = productQueryUseCase.getProductById(ProductId.of(sanitizedId))
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
    fun getProductsByCategory(@PathVariable @NotBlank category: String): List<ProductResponse> {
        val sanitized = InputSanitizer.sanitizePathSegment(category)
        return productQueryUseCase.getProductsByCategory(sanitized)
            .map { ProductResponse.fromDomain(it) }
    }

    @GetMapping("/price-range")
    fun getProductsByPriceRange(
        @RequestParam @DecimalMin("0.0") minPrice: BigDecimal,
        @RequestParam @DecimalMin("0.0") maxPrice: BigDecimal
    ): List<ProductResponse> {
        if (minPrice > maxPrice) {
            throw IllegalArgumentException("minPrice must be less than or equal to maxPrice")
        }
        return productQueryUseCase.getProductsByPriceRange(Price.of(minPrice), Price.of(maxPrice))
            .map { ProductResponse.fromDomain(it) }
    }

    @GetMapping("/category/{category}/price-range")
    fun getProductsByCategoryAndPriceRange(
        @PathVariable @NotBlank category: String,
        @RequestParam @DecimalMin("0.0") minPrice: BigDecimal,
        @RequestParam @DecimalMin("0.0") maxPrice: BigDecimal
    ): List<ProductResponse> {
        if (minPrice > maxPrice) {
            throw IllegalArgumentException("minPrice must be less than or equal to maxPrice")
        }
        val sanitized = InputSanitizer.sanitizePathSegment(category)
        return productQueryUseCase.getProductsByCategoryAndPriceRange(sanitized, Price.of(minPrice), Price.of(maxPrice))
            .map { ProductResponse.fromDomain(it) }
    }
}
