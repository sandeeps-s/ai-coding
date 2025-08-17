package com.ai.coding.materializedview.health

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class KafkaHealthIndicator(
    private val env: Environment
) : HealthIndicator {

    override fun health(): Health = try {
        val bootstrap = resolveBootstrapServers()
            ?: return Health.unknown().withDetail("reason", "bootstrap.servers not configured").build()

        val props = mapOf(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrap,
            AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG to "2000"
        )
        AdminClient.create(props).use { admin ->
            val nodes = admin.describeCluster().nodes().get(2, TimeUnit.SECONDS)
            val nodeCount = nodes.size
            if (nodeCount > 0) {
                Health.up()
                    .withDetail("bootstrapServers", bootstrap)
                    .withDetail("brokers", nodeCount)
                    .withDetail("nodeIds", nodes.map { it.idString() })
                    .build()
            } else {
                Health.down()
                    .withDetail("bootstrapServers", bootstrap)
                    .withDetail("brokers", nodeCount)
                    .build()
            }
        }
    } catch (ex: Exception) {
        Health.down(ex).build()
    }

    private fun resolveBootstrapServers(): String? {
        // Prefer standard Spring Kafka property if present
        val direct = env.getProperty("spring.kafka.bootstrap-servers")
        if (!direct.isNullOrBlank()) return direct
        // Fallback to Spring Cloud Stream Kafka Streams binder property used in this app
        return env.getProperty("spring.cloud.stream.kafka.streams.binder.configuration.bootstrap.servers")
    }
}
