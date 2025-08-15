package com.ai.coding.materializedview.domain.event

import com.ai.coding.materializedview.domain.model.Product
import com.ai.coding.materializedview.domain.model.value.ProductId

sealed interface ProductEvent : DomainEvent

data class ProductCreated(val product: Product) : ProductEvent

data class ProductUpdated(val product: Product) : ProductEvent

data class ProductDeleted(val productId: ProductId) : ProductEvent

