package com.ai.coding.materializedview.infrastructure.adapter.inbound.web

import com.ai.coding.materializedview.domain.exception.ExternalDependencyException
import com.ai.coding.materializedview.domain.exception.InvalidMessageException
import com.ai.coding.materializedview.domain.exception.PersistenceException
import com.ai.coding.materializedview.domain.exception.ProcessingException
import com.ai.coding.materializedview.infrastructure.adapter.inbound.web.dto.ApiErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException, request: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        val errors = ex.constraintViolations.associate { v ->
            val field = v.propertyPath?.toString() ?: "param"
            field to (v.message ?: "Invalid value")
        }
        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, errors)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException, request: HttpServletRequest): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.BAD_REQUEST, "Missing parameter '${ex.parameterName}'", request)

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException, request: HttpServletRequest): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.BAD_REQUEST, "Invalid value for '${ex.name}'", request)

    @ExceptionHandler(IllegalArgumentException::class, InvalidMessageException::class)
    fun handleBadRequest(ex: RuntimeException, request: HttpServletRequest): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request", request)

    @ExceptionHandler(PersistenceException::class, DataAccessException::class)
    fun handlePersistence(ex: Exception, request: HttpServletRequest): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.SERVICE_UNAVAILABLE, "Persistence error", request)

    @ExceptionHandler(ExternalDependencyException::class)
    fun handleExternal(ex: ExternalDependencyException, request: HttpServletRequest): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.SERVICE_UNAVAILABLE, ex.message ?: "External dependency error", request)

    @ExceptionHandler(ProcessingException::class)
    fun handleProcessing(ex: ProcessingException, request: HttpServletRequest): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.INTERNAL_SERVER_ERROR, ex.message ?: "Processing error", request)

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception, request: HttpServletRequest): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.INTERNAL_SERVER_ERROR, ex.message ?: "Unexpected error", request)

    private fun build(status: HttpStatus, message: String, request: HttpServletRequest, errors: Map<String, String>? = null): ResponseEntity<ApiErrorResponse> {
        val body = ApiErrorResponse(
            timestamp = Instant.now(),
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = request.requestURI,
            errors = errors
        )
        return ResponseEntity.status(status).body(body)
    }
}

