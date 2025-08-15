package com.ai.coding.materializedview.infrastructure.adapter.shared

import com.ai.coding.materializedview.domain.exception.ExternalDependencyException
import com.ai.coding.materializedview.domain.exception.InvalidMessageException
import com.ai.coding.materializedview.domain.exception.PersistenceException
import com.ai.coding.materializedview.domain.exception.ProcessingException
import org.springframework.dao.DataAccessException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * Maps low-level infrastructure and runtime exceptions to domain-specific exceptions.
 */
object ExceptionMapper {
    fun map(throwable: Throwable): RuntimeException = when (throwable) {
        is InvalidMessageException -> throwable
        is DataAccessException -> PersistenceException(throwable.message ?: "Data access error", throwable)
        is com.aerospike.client.AerospikeException -> PersistenceException(throwable.message ?: "Aerospike error", throwable)
        is ConnectException, is SocketTimeoutException, is TimeoutException -> ExternalDependencyException(
            throwable.message ?: "External dependency timeout", throwable
        )
        is IOException -> ExternalDependencyException(throwable.message ?: "I/O error", throwable)
        else -> ProcessingException(throwable.message ?: "Processing error", throwable)
    }
}
