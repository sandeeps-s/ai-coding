package com.ai.coding.materializedview.config

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import org.apache.kafka.streams.StreamsConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class StreamConfiguration {

    @Value("\${spring.cloud.stream.kafka.streams.binder.configuration.schema.registry.url:http://localhost:8081}")
    private lateinit var schemaRegistryUrl: String

    @Bean
    fun streamConfiguration(): Properties {
        val props = Properties()

        // Avro configuration
        props[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryUrl
        props[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = false

        // Additional Kafka Streams configuration
        props[StreamsConfig.PROCESSING_GUARANTEE_CONFIG] = StreamsConfig.EXACTLY_ONCE_V2
        props[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = 1000

        return props
    }
}
