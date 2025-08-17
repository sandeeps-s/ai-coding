package com.ai.coding.materializedview.infrastructure.adapter.inbound.messaging

import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.Message
import org.springframework.messaging.support.ErrorMessage

/**
 * Routes failed messages to a dead-letter destination using StreamBridge.
 * Works across binders and also in tests with the test binder.
 */
@Configuration
class DeadLetterConfig(
    private val streamBridge: StreamBridge
) {
    private val log = LoggerFactory.getLogger(DeadLetterConfig::class.java)

    // Error channel name pattern: <bindingName>.errors
    @ServiceActivator(inputChannel = "processProductChange-in-0.errors")
    fun handleBindingErrors(error: ErrorMessage) {
        routeToDlq(error)
    }

    // Fallback global error channel to catch any unhandled errors
    @ServiceActivator(inputChannel = "errorChannel")
    fun handleGlobalErrors(error: ErrorMessage) {
        routeToDlq(error)
    }

    private fun routeToDlq(error: ErrorMessage) {
        val failed: Message<*>? = error.originalMessage
        val cause = error.payload
        log.error("Routing message to DLQ due to error: {}", cause.message, cause)

        if (failed != null) {
            // Send to dedicated DLQ binding (configured to product-changes.DLT)
            val sent = streamBridge.send("productChangesDlq-out-0", failed)
            if (!sent) {
                log.warn("Failed to send message to DLQ binding productChangesDlq-out-0")
            }
        } else {
            log.warn("No original message found on error, skipping DLQ publish")
        }
    }
}
