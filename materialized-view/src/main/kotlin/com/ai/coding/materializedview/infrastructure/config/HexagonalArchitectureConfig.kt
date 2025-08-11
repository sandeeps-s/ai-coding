package com.ai.coding.materializedview.infrastructure.config

import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository
import com.ai.coding.materializedview.domain.service.ProductDomainService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for hexagonal architecture
 * This wires together the ports and adapters
 */
@Configuration
class HexagonalArchitectureConfig {

    @Bean
    fun productQueryUseCase(productRepository: ProductRepository): ProductQueryUseCase {
        return ProductDomainService(productRepository)
    }

    @Bean
    fun productCommandUseCase(productRepository: ProductRepository): ProductCommandUseCase {
        return ProductDomainService(productRepository)
    }
}
