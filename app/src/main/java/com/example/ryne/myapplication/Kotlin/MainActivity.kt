package com.example.ryne.myapplication.Kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.ryne.myapplication.Java.Constant
import com.example.ryne.myapplication.Kotlin.response.ProductResponse
import com.example.ryne.myapplication.R
import kotlinx.android.synthetic.main.activity_main_jav.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_jav)

        btnFetch.setOnClickListener {
            val callResponse = ApiClient().getProducts(Constant.token)
            callResponse.enqueue(object : Callback<List<ProductResponse>> {
                override fun onResponse(call: Call<List<ProductResponse>>?, response: Response<List<ProductResponse>>?) {
                    Utils.showToast(applicationContext, response!!.body().toString())
                }

                override fun onFailure(call: Call<List<ProductResponse>>?, t: Throwable?) {
                    Utils.showToast(applicationContext, "fail")
                }
            })
        }
    }
}
