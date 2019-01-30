package com.example.ryne.myapplication.Kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.ryne.myapplication.Java.Constant
import com.example.ryne.myapplication.Kotlin.adapter.TaxonAdapter
import com.example.ryne.myapplication.Kotlin.entity.request.Product
import com.example.ryne.myapplication.Kotlin.entity.request.response.ListProductResponse
import com.example.ryne.myapplication.Kotlin.entity.request.response.ProductResponse
import com.example.ryne.myapplication.Kotlin.entity.response.ImageResponse
import com.example.ryne.myapplication.Kotlin.entity.response.Taxon
import com.example.ryne.myapplication.Kotlin.entity.response.TaxonResponse
import com.example.ryne.myapplication.R
import com.google.gson.Gson
import com.google.gson.JsonArray
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

    lateinit var taxonAdapter: TaxonAdapter
    var taxonId: String = ""


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
            if (lstProduct == null && lstProduct.isEmpty()) {
                Utils.showToast(applicationContext, "Please read csv file before upload!")
            } else {
                var product = lstProduct.get(nextProduct)
                uploadProduct(product)
            }
        }

        taxonAdapter = TaxonAdapter(applicationContext, R.layout.item_taxon, R.id.tv_taxon)
        spinnerTaxon.adapter = taxonAdapter
        spinnerTaxon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                taxonId = (p0!!.getItemAtPosition(p2) as Taxon).id!!
                Utils.showToast(applicationContext, taxonId)
            }

        }
        getTaxons()
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
        productJson.addProperty("price", ProductUtils.increasePriceItemRandomly(product.productPrice!!))
        productJson.addProperty("description", product.productDescription1)
        productJson.addProperty("total_on_hand", Constant.TOTAL_IN_HAND)
        productJson.addProperty("shipping_category_id", "1") // Defaut
        // taxons array
        val jsonArray = JsonArray()
        jsonArray.add(taxonId)
        productJson.add("taxon_ids", jsonArray)
        val productElement = Gson().fromJson(productJson, JsonElement::class.java)
        val jsonObject = JsonObject()
        jsonObject.add("product", productElement)
        val callResponse = ApiClient().createProduct(Constant.token, jsonObject)
        callResponse.enqueue(object : Callback<ProductResponse> {

            override fun onResponse(call: Call<ProductResponse>?, response: Response<ProductResponse>?) {
                if (response!!.isSuccessful) {
                    lstImageUrl = getListImageURL(product)
                    if (!lstImageUrl.isEmpty()) {
                        uploadProductImageViaURL(response.body().slug, 0)
                    }
                }
            }

            override fun onFailure(call: Call<ProductResponse>?, t: Throwable?) {
                Utils.showToast(applicationContext, "created product fail")
                // if fail, keep upload

                // add the fail product here to DB for tracking later

                uploadNextProduct()
            }
        })
    }

    fun uploadNextProduct() {
        if (nextProduct < lstProduct.size) {
            uploadProduct(lstProduct.get(nextProduct))
        } else {
            tvNotice.setText("DONE UPLOADED")
            Utils.showToast(applicationContext, "Uploaded: " + lstProduct.size + " items")
        }
    }

    fun uploadProductImageViaURL(splug: String, currentIndex: Int) {
        if (currentIndex < lstImageUrl.size) {
            var url = lstImageUrl.get(currentIndex)
            val jsonObject = JsonObject()
            jsonObject.addProperty("url", url)
            val callResponse = ApiClient().uploadImage(splug, Constant.token, jsonObject)
            callResponse.enqueue(object : Callback<ImageResponse> {
                override fun onFailure(call: Call<ImageResponse>?, t: Throwable?) {
                    var index = currentIndex
                    index++
                    if (index < lstImageUrl.size) {
                        // if still have image product, keep upload
                        uploadProductImageViaURL(splug, index)
                    } else {// keep upload product
                        uploadNextProduct()
                    }

                }

                override fun onResponse(call: Call<ImageResponse>?, response: Response<ImageResponse>?) {
                    if (response!!.isSuccessful) {
                        Utils.glideUrl(applicationContext, response.body().productUrl, imvDownload)
                    }

                    var index = currentIndex
                    index++;
                    if (index < lstImageUrl.size) {
                        uploadProductImageViaURL(splug, index)
                    } else {
                        uploadNextProduct()
                    }
                }

            })
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
        if (product.productImage5 != null) {
            staticUrls.add(product.productImage5!!)
        }
        if (product.productImage6 != null) {
            staticUrls.add(product.productImage6!!)
        }
        if (product.productImage7 != null) {
            staticUrls.add(product.productImage7!!)
        }
        if (product.productImage8 != null) {
            staticUrls.add(product.productImage8!!)
        }
        if (product.productImage9 != null) {
            staticUrls.add(product.productImage9!!)
        }
        if (product.productImage10 != null) {
            staticUrls.add(product.productImage10!!)
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

    fun getTaxons() {
        val callResponse = ApiClient().getTaxons(Constant.token)
        callResponse.enqueue(object : Callback<TaxonResponse> {
            override fun onFailure(call: Call<TaxonResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<TaxonResponse>?, response: Response<TaxonResponse>?) {
                taxonAdapter.setTaxons(response!!.body().taxons)
            }

        })
    }
}
