package com.example.ryne.myapplication.Kotlin

import android.content.Context
import android.util.Log
import android.widget.Toast

class Utils {
    companion object {
        fun showToast(context: Context, message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        fun log(message: String) {
            Log.d("LOG", message)
        }

        fun indexExists(array: Array<String>, index: Int): Boolean = index >= 0 && index < array.size
    }
}