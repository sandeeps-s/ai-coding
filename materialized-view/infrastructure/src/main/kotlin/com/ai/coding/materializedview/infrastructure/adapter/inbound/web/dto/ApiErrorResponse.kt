package com.ai.coding.materializedview.infrastructure.adapter.inbound.web.dto

import java.time.Instant

/**
 * Standard API error payload returned by the GlobalExceptionHandler.
 */
data class ApiErrorResponse(
    val timestamp: Instant,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val errors: Map<String, String>? = null
)

