package com.ai.coding.materializedview.infrastructure.adapter.shared

import com.ai.coding.materializedview.domain.exception.ExternalDependencyException
import com.ai.coding.materializedview.domain.exception.InvalidMessageException
import com.ai.coding.materializedview.domain.exception.PersistenceException
import com.ai.coding.materializedview.domain.exception.ProcessingException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ExceptionMapperTest {

    @Test
    fun `should return InvalidMessageException as is`() {
        val ex = InvalidMessageException("oops")
        val mapped = ExceptionMapper.map(ex)
        assertTrue(mapped === ex)
    }

    @Test
    fun `should map IllegalArgumentException to InvalidMessageException`() {
        val mapped = ExceptionMapper.map(IllegalArgumentException("bad arg"))
        assertTrue(mapped is InvalidMessageException)
    }

    @Test
    fun `should map DataAccessException to PersistenceException`() {
        val mapped = ExceptionMapper.map(DataIntegrityViolationException("db"))
        assertTrue(mapped is PersistenceException)
    }

    @Test
    fun `should map AerospikeException to PersistenceException`() {
        val ae = com.aerospike.client.AerospikeException(1, "aero")
        val mapped = ExceptionMapper.map(ae)
        assertTrue(mapped is PersistenceException)
    }

    @Test
    fun `should map timeout exceptions to ExternalDependencyException`() {
        assertTrue(ExceptionMapper.map(ConnectException("conn")) is ExternalDependencyException)
        assertTrue(ExceptionMapper.map(SocketTimeoutException("socket")) is ExternalDependencyException)
        assertTrue(ExceptionMapper.map(TimeoutException("timeout")) is ExternalDependencyException)
    }

    @Test
    fun `should map IOException to ExternalDependencyException`() {
        val mapped = ExceptionMapper.map(IOException("io"))
        assertTrue(mapped is ExternalDependencyException)
    }

    @Test
    fun `should map unknown exceptions to ProcessingException`() {
        val mapped = ExceptionMapper.map(RuntimeException("other"))
        assertTrue(mapped is ProcessingException)
    }
}

