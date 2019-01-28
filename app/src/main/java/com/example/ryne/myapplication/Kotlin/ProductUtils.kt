package com.example.ryne.myapplication.Kotlin

import java.util.*

class ProductUtils {
    companion object {
        fun increasePriceItemRandomly(price: String) = when {
            Random().nextBoolean() -> price.toDouble() * 30 / 100
            else -> {
                price.toDouble() * 20 / 100
            }
        }
    }
}