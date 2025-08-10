package com.ai.coding.materializedview

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MaterializedViewApplication

// Functional main function using top-level function
fun main(args: Array<String>) = runApplication<MaterializedViewApplication>(*args).let { }
