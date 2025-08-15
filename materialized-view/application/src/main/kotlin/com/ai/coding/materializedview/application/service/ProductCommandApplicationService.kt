package com.ai.coding.materializedview.application.service

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase

/**
 * Application service orchestrating product command use cases.
 * Delegates business rules to the domain layer.
 */
class ProductCommandApplicationService(
    private val domainCommands: ProductCommandUseCase
) : ProductCommandUseCase {

    override fun createProduct(product: Product): Product =
        domainCommands.createProduct(product)

    override fun updateProduct(product: Product): Product =
        domainCommands.updateProduct(product)

    override fun deleteProduct(productId: String) =
        domainCommands.deleteProduct(productId)
}
