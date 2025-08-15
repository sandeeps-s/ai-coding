package com.ai.coding.materializedview.config

import com.ai.coding.materializedview.application.service.ProductCommandApplicationService
import com.ai.coding.materializedview.application.service.ProductQueryApplicationService
import com.ai.coding.materializedview.domain.port.inbound.ProductCommandUseCase
import com.ai.coding.materializedview.domain.port.inbound.ProductQueryUseCase
import com.ai.coding.materializedview.domain.port.outbound.ProductRepository
import com.ai.coding.materializedview.domain.service.ProductDomainService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for hexagonal architecture in multi-module setup
 * Wires application services to the domain service and adapters.
 */
@Configuration
class HexagonalArchitectureConfig {

    @Bean
    fun productDomainService(productRepository: ProductRepository): ProductDomainService =
        ProductDomainService(productRepository)

    @Bean
    fun productQueryUseCase(domain: ProductDomainService): ProductQueryUseCase =
        ProductQueryApplicationService(domain)

    @Bean
    fun productCommandUseCase(domain: ProductDomainService): ProductCommandUseCase =
        ProductCommandApplicationService(domain)
}
