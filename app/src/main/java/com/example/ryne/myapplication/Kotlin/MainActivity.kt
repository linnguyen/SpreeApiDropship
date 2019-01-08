package com.example.ryne.myapplication.Kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ryne.myapplication.Java.Constant
import com.example.ryne.myapplication.Kotlin.entity.request.Product
import com.example.ryne.myapplication.Kotlin.entity.request.response.ListProductResponse
import com.example.ryne.myapplication.Kotlin.entity.request.response.ProductResponse
import com.example.ryne.myapplication.R
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.opencsv.CSVReader
import kotlinx.android.synthetic.main.activity_main_jav.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    var lstProduct: MutableList<Product> = mutableListOf()
    var nextProduct = 0
    var lstImageUrl: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_jav)

        btnFetch.setOnClickListener {
            val callResponse = ApiClient().getProducts(Constant.token)
            callResponse.enqueue(object : Callback<ListProductResponse> {
                override fun onResponse(call: Call<ListProductResponse>?, response: Response<ListProductResponse>?) {
                    Utils.showToast(applicationContext, response!!.body().toString())
                }

                override fun onFailure(call: Call<ListProductResponse>?, t: Throwable?) {
                    Utils.showToast(applicationContext, "fail")
                }
            })
        }

        btnRead.setOnClickListener {
            Utils.showToast(applicationContext, "Click Read")
            readProductDataLib()
        }

        btnCreate.setOnClickListener {
            if (lstProduct.isEmpty()) {
                Utils.showToast(applicationContext, "Please read csv file before upload!")
            } else {
                var product = lstProduct.get(nextProduct)
                uploadProduct(product)
            }
        }
    }

    fun uploadProduct(product: Product) {
        nextProduct++
        //
        tvUpload.text = "Uploading: " + product.id
        //
        tvNotice.text = "UPLOADING!!"

        var productJson = JsonObject()
        productJson.addProperty("id", product.id)
        productJson.addProperty("name", product.productName)
        productJson.addProperty("price", product.productPrice)
        productJson.addProperty("description", product.productDescription1)
        productJson.addProperty("shipping_category", 1)
        val productElement = Gson().fromJson(productJson, JsonElement::class.java)
        val jsonObject = JsonObject()
        jsonObject.add("product", productElement)
        val callResponse = ApiClient().createProduct(Constant.token, jsonObject)
        callResponse.enqueue(object : Callback<ProductResponse> {
            override fun onFailure(call: Call<ProductResponse>?, t: Throwable?) {
                Utils.showToast(applicationContext, "created product fail")
            }

            override fun onResponse(call: Call<ProductResponse>?, response: Response<ProductResponse>?) {
                if (response!!.isSuccessful) {
                    lstImageUrl = getListImageURL(product)
                    if (!lstImageUrl.isEmpty()) {
                        downloadImageThenUpload(response.body().slug, 0)
                    }
                }
            }
        })
    }

    fun downloadImageThenUpload(splug: String, index: Int) {
        if (index < lstImageUrl.size) {
            var url = lstImageUrl.get(index)
            Glide.with(this)
                    .load(url)
                    .into(imvDownload)
        }
    }

    fun getListImageURL(product: Product): MutableList<String> {
        var staticUrls: MutableList<String> = mutableListOf()

        if (product.productImage1 != null) {
            staticUrls.add(product.productImage1!!)
        }

        if (product.productImage2 != null) {
            staticUrls.add(product.productImage2!!)
        }
        if (product.productImage3 != null) {
            staticUrls.add(product.productImage3!!)
        }
        if (product.productImage4 != null) {
            staticUrls.add(product.productImage4!!)
        }
        return staticUrls
    }

    fun readProductDataLib(): List<Product>? {
        // clear before read lines
        lstProduct.clear()

        val inputStream = resources.openRawResource(R.raw.product_test)

        val reader = BufferedReader(InputStreamReader(inputStream))

        val csvReader = CSVReader(reader)

        var nextLine: Array<String>?

        var count = 0

        // can not assign nextLine in condition check
        do {
            nextLine = csvReader.readNext()
            if (count == 0) {
                count++
                continue
            }

            if (nextLine == null) {
                break
            }
            var product = Product()
            // Setters
            product.id = nextLine[0]
            product.productName = nextLine[1]
            product.categoryName = nextLine[2]
            product.categoryUrl = nextLine[3]
            product.subCategoryName = nextLine[4]
            product.subCategoryUrl = nextLine[5]
            product.dateProductWasLaunched = nextLine[6]
            product.currency = nextLine[7]
            product.productPrice = nextLine[8]
            product.weight = nextLine[9]
            product.productDescription1 = nextLine[10]
            product.productDescription2 = nextLine[11]
            product.productDescription3 = nextLine[12]
            product.productUrl = nextLine[13]
            product.warehouse = nextLine[14]
            product.options = nextLine[15]
            if (Utils.indexExists(nextLine, 16)) {
                product.productImage1 = nextLine[16]
            }
            if (Utils.indexExists(nextLine, 17)) {
                product.productImage2 = nextLine[17]
            }
            if (Utils.indexExists(nextLine, 18)) {
                product.productImage3 = nextLine[18]
            }
            if (Utils.indexExists(nextLine, 19)) {
                product.productImage4 = nextLine[19]
            }
            lstProduct.add(product)
            count++

        } while (true)

        Utils.showToast(applicationContext, "Total items " + lstProduct.size)

        return lstProduct
    }
}
