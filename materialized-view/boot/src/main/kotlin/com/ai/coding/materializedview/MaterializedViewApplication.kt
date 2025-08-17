package com.ai.coding.materializedview

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class MaterializedViewApplication

// Functional main function using top-level function
fun main(args: Array<String>) = runApplication<MaterializedViewApplication>(*args).let { }
