package com.ai.coding.materializedview.web

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.Price
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.infrastructure.adapter.inbound.web.ProductQueryController
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get
import org.mockito.BDDMockito.given
import java.math.BigDecimal

@WebMvcTest(controllers = [ProductQueryController::class])
@AutoConfigureMockMvc(addFilters = false)
class ProductQueryControllerSliceTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var productQueryUseCase: ProductQueryUseCase

    private fun product(
        id: String = "test-1",
        name: String = "Test Product",
        description: String? = "Desc",
        price: BigDecimal? = BigDecimal("9.99"),
        category: String? = "Category",
        version: Long = 1
    ): Product = Product.create(
        productId = ProductId.of(id),
        name = ProductName.of(name),
        description = description,
        price = price?.let { Price.of(it) },
        category = category,
        version = version
    )

    @Test
    fun `getProduct returns 200 with body when found`() {
        val p = product("found-1")
        given(productQueryUseCase.getProductById(ProductId.of("found-1"))).willReturn(p)

        mockMvc.get("/api/v1/products/found-1") { accept = MediaType.APPLICATION_JSON }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("$.productId", equalTo("found-1")) }
            .andExpect { jsonPath("$.name", equalTo(p.name.value)) }
            .andExpect { jsonPath("$.description", equalTo(p.description)) }
            .andExpect { jsonPath("$.price", equalTo(p.price!!.value.toDouble())) }
            .andExpect { jsonPath("$.category", equalTo(p.category)) }
            .andExpect { jsonPath("$.version", equalTo(p.version.toInt())) }
    }

    @Test
    fun `getProduct returns 404 when not found`() {
        given(productQueryUseCase.getProductById(ProductId.of("missing"))).willReturn(null)

        mockMvc.get("/api/v1/products/missing") { accept = MediaType.APPLICATION_JSON }
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `getProductsByCategory returns list`() {
        given(productQueryUseCase.getProductsByCategory("Cat")).willReturn(listOf(product(id = "p-1", category = "Cat")))

        mockMvc.get("/api/v1/products/category/Cat") { accept = MediaType.APPLICATION_JSON }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("$", hasSize<Any>(1)) }
            .andExpect { jsonPath("$[0].productId", equalTo("p-1")) }
    }

    @Test
    fun `getAllProducts returns list`() {
        given(productQueryUseCase.getAllProducts()).willReturn(listOf(product("p-1"), product("p-2")))

        mockMvc.get("/api/v1/products") { accept = MediaType.APPLICATION_JSON }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("$", hasSize<Any>(2)) }
            .andExpect { jsonPath("$[0].productId", equalTo("p-1")) }
            .andExpect { jsonPath("$[1].productId", equalTo("p-2")) }
    }

    @Test
    fun `getProductsByCategoryAndPriceRange returns list`() {
        val min = BigDecimal("5.00")
        val max = BigDecimal("15.00")
        given(productQueryUseCase.getProductsByCategoryAndPriceRange("Cat", Price.of(min), Price.of(max)))
            .willReturn(listOf(product(id = "p-1", category = "Cat", price = BigDecimal("9.99"))))

        mockMvc.get("/api/v1/products/category/Cat/price-range") {
            accept = MediaType.APPLICATION_JSON
            param("minPrice", min.toPlainString())
            param("maxPrice", max.toPlainString())
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("$", hasSize<Any>(1)) }
            .andExpect { jsonPath("$[0].productId", equalTo("p-1")) }
    }

    @Test
    fun `getProductsByPriceRange returns 400 when min greater than max`() {
        mockMvc.get("/api/v1/products/price-range") {
            accept = MediaType.APPLICATION_JSON
            param("minPrice", "10.00")
            param("maxPrice", "5.00")
        }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    fun `legacy getProduct path works`() {
        val p = product("legacy-1")
        given(productQueryUseCase.getProductById(ProductId.of("legacy-1"))).willReturn(p)

        mockMvc.get("/api/products/legacy-1") { accept = MediaType.APPLICATION_JSON }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("$.productId", equalTo("legacy-1")) }
    }
}
