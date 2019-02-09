package com.example.ryne.myapplication.Kotlin

import java.util.*

class ProductUtils {
    companion object {
        fun increasePriceItemRandomly(price: String) = when {
            Random().nextBoolean() -> price.toDouble() * 50 / 100
            else -> {
                price.toDouble() * 40 / 100
            }
        }
    }
}