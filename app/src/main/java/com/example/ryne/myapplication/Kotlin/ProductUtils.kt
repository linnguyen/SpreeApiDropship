package com.example.ryne.myapplication.Kotlin

import java.util.*

class ProductUtils {
    companion object {
        fun increasePriceItemRandomly(price: String) = when {
            Random().nextBoolean() -> price.toDouble() + price.toDouble() * 80 / 100
            else -> {
                price.toDouble() + price.toDouble() * 75 / 100
            }
        }

        fun isStringNotEmpty(string: String): Boolean {
            return !string.equals("")
        }
    }
}