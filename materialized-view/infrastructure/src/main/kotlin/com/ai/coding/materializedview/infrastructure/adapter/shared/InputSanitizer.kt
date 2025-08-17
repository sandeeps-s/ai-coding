package com.ai.coding.materializedview.infrastructure.adapter.shared

object InputSanitizer {
    private val CONTROL_CHARS = Regex("[\\p{Cntrl}&&[^\\r\\n\\t]]") // keep CR, LF, TAB
    private val ZERO_WIDTH = Regex("[\\u200B-\\u200D\\uFEFF]")
    private val SCRIPT_TAG = Regex("(?i)<\\s*/?\\s*script\\b[^>]*>")

    fun sanitizeText(input: String?): String? {
        if (input == null) return null
        var s = input.trim()
        if (s.isEmpty()) return s
        s = s.replace(CONTROL_CHARS, "")
        s = s.replace(ZERO_WIDTH, "")
        s = s.replace(SCRIPT_TAG, "")
        return s
    }

    fun sanitizeParam(input: String?): String? = sanitizeText(input)

    fun sanitizePathSegment(input: String?): String {
        val s = sanitizeText(input) ?: ""
        if (s.contains('/')) throw IllegalArgumentException("Invalid path segment")
        return s
    }
}
