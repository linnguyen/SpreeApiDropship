package com.example.ryne.myapplication.Kotlin

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import com.example.ryne.myapplication.Java.UnsafeOkHttpClient
import java.io.InputStream


@GlideModule
class AppGlideModule : LibraryGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = UnsafeOkHttpClient.getUnsafeOkHttpClient()
        registry.replace(GlideUrl::class.java, InputStream::class.java,
                OkHttpUrlLoader.Factory(client))
    }

//    open fun unsafeOkHttpClient(): OkHttpClient {
//        val unsafeTrustManager = createUnsafeTrustManager()
//        val sslContext = SSLContext.getInstance("SSL")
//        sslContext.init(null, arrayOf(unsafeTrustManager), null)
//        return OkHttpClient.Builder()
//                .sslSocketFactory(sslContext.socketFactory, unsafeTrustManager)
//                .hostnameVerifier { hostName, sslSession -> true }
//                .build()
//    }
//
//    fun createUnsafeTrustManager(): X509TrustManager {
//        return object : X509TrustManager {
//            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
//            }
//
//            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
//            }
//
//            override fun getAcceptedIssuers(): Array<out X509Certificate>? {
//                return emptyArray()
//            }
//        }
//    }

}
