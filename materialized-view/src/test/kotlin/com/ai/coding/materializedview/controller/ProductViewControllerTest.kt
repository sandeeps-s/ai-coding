package com.ai.coding.materializedview.controller

import com.ai.coding.materializedview.dto.ProductViewResponse
import com.ai.coding.materializedview.service.ProductQueryService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Instant
import java.util.*

@WebMvcTest(ProductViewController::class)
class ProductViewControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun productQueryService(): ProductQueryService = mockk()
    }

    @Autowired
    private lateinit var productQueryService: ProductQueryService

    @Test
    fun `should return product when found`() {
        // Given
        val productId = "product-123"
        val productResponse = ProductViewResponse(
            productId = productId,
            name = "Test Product",
            description = "Test Description",
            price = 99.99,
            category = "Electronics",
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            version = 1
        )
        every { productQueryService.findProductById(productId) } returns Optional.of(productResponse)

        // When & Then
        mockMvc.perform(get("/api/products/{productId}", productId))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.productId").value(productId))
            .andExpect(jsonPath("$.name").value("Test Product"))
            .andExpect(jsonPath("$.price").value(99.99))
    }

    @Test
    fun `should return 404 when product not found`() {
        // Given
        val productId = "non-existent"
        every { productQueryService.findProductById(productId) } returns Optional.empty()

        // When & Then
        mockMvc.perform(get("/api/products/{productId}", productId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return products by category`() {
        // Given
        val category = "Electronics"
        val products = listOf(
            ProductViewResponse(
                productId = "product-1",
                name = "Product 1",
                description = null,
                price = 50.0,
                category = category,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                version = 1
            )
        )
        every { productQueryService.findProductsByCategory(category) } returns products

        // When & Then
        mockMvc.perform(get("/api/products/category/{category}", category))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].category").value(category))
    }

    @Test
    fun `should return products by price range`() {
        // Given
        val minPrice = 10.0
        val maxPrice = 100.0
        val products = listOf(
            ProductViewResponse(
                productId = "product-1",
                name = "Product 1",
                description = null,
                price = 50.0,
                category = "Electronics",
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                version = 1
            )
        )
        every { productQueryService.findProductsByPriceRange(minPrice, maxPrice) } returns products

        // When & Then
        mockMvc.perform(
            get("/api/products/price-range")
                .param("minPrice", minPrice.toString())
                .param("maxPrice", maxPrice.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
    }
}
