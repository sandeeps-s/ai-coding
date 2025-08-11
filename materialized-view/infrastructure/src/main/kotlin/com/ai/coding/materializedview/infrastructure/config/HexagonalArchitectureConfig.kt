package com.ai.coding.materializedview.infrastructure.config

import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.application.service.ProductQueryApplicationService
import com.ai.coding.materializedview.application.service.ProductCommandApplicationService
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for hexagonal architecture in multi-module setup
 * This wires together the application services with the domain ports
 */
@Configuration
class HexagonalArchitectureConfig {

    @Bean
    fun productQueryUseCase(productRepository: ProductRepository): ProductQueryUseCase {
        return ProductQueryApplicationService(productRepository)
    }

    @Bean
    fun productCommandUseCase(productRepository: ProductRepository): ProductCommandUseCase {
        return ProductCommandApplicationService(productRepository)
    }
}
