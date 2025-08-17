package com.ai.coding.materializedview.health

import com.aerospike.client.AerospikeClient
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class AerospikeHealthIndicator(
    private val client: AerospikeClient
) : HealthIndicator {
    override fun health(): Health = try {
        val nodes = client.nodes
        val active = nodes.count { it.isActive }
        if (active > 0) {
            Health.up()
                .withDetail("nodes", nodes.size)
                .withDetail("activeNodes", active)
                .withDetail("nodeNames", nodes.map { it.name })
                .build()
        } else {
            Health.down()
                .withDetail("nodes", nodes.size)
                .withDetail("activeNodes", active)
                .build()
        }
    } catch (ex: Exception) {
        Health.down(ex).build()
    }
}

