package com.ai.coding.materializedview.infrastructure.event

import com.ai.coding.materializedview.domain.event.ProductCreated
import com.ai.coding.materializedview.domain.event.ProductDeleted
import com.ai.coding.materializedview.domain.event.ProductUpdated
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class LoggingProductEventListener {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onCreated(event: ProductCreated) {
        log.info("ProductCreated event: id={}, version={}", event.product.productId, event.product.version)
    }

    @EventListener
    fun onUpdated(event: ProductUpdated) {
        log.info("ProductUpdated event: id={}, version={}", event.product.productId, event.product.version)
    }

    @EventListener
    fun onDeleted(event: ProductDeleted) {
        log.info("ProductDeleted event: id={}", event.productId)
    }
}

