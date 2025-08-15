package com.ai.coding.materializedview.domain.event

fun interface DomainEventPublisher {
    fun publish(event: DomainEvent)
}

