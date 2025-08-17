package com.ai.coding.materializedview.infrastructure.adapter.inbound.web

import com.ai.coding.materializedview.infrastructure.adapter.shared.InputSanitizer
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SanitizingFilter : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val wrapped = SanitizingRequestWrapper(request)
        filterChain.doFilter(wrapped, response)
    }
}

private class SanitizingRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    override fun getParameter(name: String?): String? {
        val raw = super.getParameter(name)
        return InputSanitizer.sanitizeParam(raw)
    }

    override fun getParameterValues(name: String?): Array<String>? {
        val raw = super.getParameterValues(name) ?: return null
        return raw.map { InputSanitizer.sanitizeParam(it) ?: "" }.toTypedArray()
    }

    override fun getParameterMap(): MutableMap<String, Array<String>> {
        val map = super.getParameterMap()
        return map.mapValues { (_, v) -> v.map { InputSanitizer.sanitizeParam(it) ?: "" }.toTypedArray() }
            .toMutableMap()
    }

    override fun getHeader(name: String?): String? {
        val raw = super.getHeader(name)
        return InputSanitizer.sanitizeText(raw)
    }
}

