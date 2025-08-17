package com.ai.coding.materializedview.application.service

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.ProductId
import com.ai.coding.materializedview.domain.model.value.ProductName
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class ProductCommandApplicationServiceTest {

    @MockK
    lateinit var domainCommands: ProductCommandUseCase

    private lateinit var service: ProductCommandApplicationService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        service = ProductCommandApplicationService(domainCommands)
    }

    private fun product(id: String) = Product(
        productId = ProductId.of(id),
        name = ProductName.of("Name-$id"),
        description = null,
        price = null,
        category = null,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        version = 1
    )

    @Test
    fun `createProduct delegates`() {
        val p = product("p-1")
        every { domainCommands.createProduct(p) } returns p
        service.createProduct(p)
        verify { domainCommands.createProduct(p) }
    }

    @Test
    fun `updateProduct delegates`() {
        val p = product("p-2")
        every { domainCommands.updateProduct(p) } returns p
        service.updateProduct(p)
        verify { domainCommands.updateProduct(p) }
    }

    @Test
    fun `deleteProduct delegates`() {
        val id = ProductId.of("p-3")
        every { domainCommands.deleteProduct(id) } returns Unit
        service.deleteProduct(id)
        verify { domainCommands.deleteProduct(id) }
    }
}

