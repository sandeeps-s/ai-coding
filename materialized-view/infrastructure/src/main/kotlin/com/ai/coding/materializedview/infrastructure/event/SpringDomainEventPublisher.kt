package com.ai.coding.materializedview.infrastructure.event

import com.ai.coding.materializedview.domain.event.DomainEvent
import com.ai.coding.materializedview.domain.event.DomainEventPublisher
import org.springframework.context.ApplicationEventPublisher

/**
 * Spring-based implementation of DomainEventPublisher that publishes
 * domain events via Spring's ApplicationEventPublisher.
 */
class SpringDomainEventPublisher(
    private val delegate: ApplicationEventPublisher
) : DomainEventPublisher {
    override fun publish(event: DomainEvent) {
        delegate.publishEvent(event)
    }
}

