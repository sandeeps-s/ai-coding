package com.ai.coding.materializedview.application.service

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class ProductQueryApplicationServiceTest {

    @MockK
    lateinit var domainQueries: ProductQueryUseCase

    private lateinit var service: ProductQueryApplicationService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        service = ProductQueryApplicationService(domainQueries)
    }

    private fun product(id: String) = Product(
        productId = ProductId.of(id),
        name = ProductName.of("Name-$id"),
        description = null,
        price = Price.of(BigDecimal("10.00")),
        category = "cat",
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        version = 1
    )

    @Test
    fun `getProductById delegates`() {
        val id = ProductId.of("p-1")
        every { domainQueries.getProductById(id) } returns product("p-1")
        service.getProductById(id)
        verify { domainQueries.getProductById(id) }
    }

    @Test
    fun `getAllProducts delegates`() {
        every { domainQueries.getAllProducts() } returns listOf(product("p-1"))
        service.getAllProducts()
        verify { domainQueries.getAllProducts() }
    }

    @Test
    fun `getProductsByCategory delegates`() {
        every { domainQueries.getProductsByCategory("cat") } returns listOf(product("p-1"))
        service.getProductsByCategory("cat")
        verify { domainQueries.getProductsByCategory("cat") }
    }

    @Test
    fun `getProductsByPriceRange delegates`() {
        val min = Price.of(BigDecimal("1"))
        val max = Price.of(BigDecimal("100"))
        every { domainQueries.getProductsByPriceRange(min, max) } returns listOf(product("p-1"))
        service.getProductsByPriceRange(min, max)
        verify { domainQueries.getProductsByPriceRange(min, max) }
    }

    @Test
    fun `getProductsByCategoryAndPriceRange delegates`() {
        val min = Price.of(BigDecimal("1"))
        val max = Price.of(BigDecimal("100"))
        every { domainQueries.getProductsByCategoryAndPriceRange("cat", min, max) } returns listOf(product("p-1"))
        service.getProductsByCategoryAndPriceRange("cat", min, max)
        verify { domainQueries.getProductsByCategoryAndPriceRange("cat", min, max) }
    }
}

