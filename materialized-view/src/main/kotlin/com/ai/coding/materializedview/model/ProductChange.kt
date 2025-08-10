package com.ai.coding.materializedview.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class ProductChange(
    @JsonProperty("productId")
    val productId: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("description")
    val description: String? = null,

    @JsonProperty("price")
    val price: Double,

    @JsonProperty("category")
    val category: String,

    @JsonProperty("changeType")
    val changeType: ChangeType,

    @JsonProperty("timestamp")
    val timestamp: Instant = Instant.now(),

    @JsonProperty("version")
    val version: Long = 1
)

enum class ChangeType {
    CREATED,
    UPDATED,
    DELETED
}
