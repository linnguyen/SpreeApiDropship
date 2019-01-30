package com.example.ryne.myapplication.Kotlin

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide

class Utils {
    companion object {
        fun showToast(context: Context, message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        fun log(message: String) {
            Log.d("LOG", message)
        }

        fun indexExists(array: Array<String>, index: Int): Boolean = index >= 0 && index < array.size

        fun glideUrl(context: Context, url: String, imageView: ImageView) {
            Glide.with(context)
                    .load(url)
                    .into(imageView)
        }
    }
}