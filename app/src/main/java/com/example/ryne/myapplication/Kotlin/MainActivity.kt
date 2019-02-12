package com.example.ryne.myapplication.Kotlin

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import com.example.ryne.myapplication.Java.Constant
import com.example.ryne.myapplication.Kotlin.adapter.TaxonAdapter
import com.example.ryne.myapplication.Kotlin.entity.request.Product
import com.example.ryne.myapplication.Kotlin.entity.request.response.ListProductResponse
import com.example.ryne.myapplication.Kotlin.entity.request.response.ProductResponse
import com.example.ryne.myapplication.Kotlin.entity.response.*
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
    var currentProduct = 0
    var lstImageUrl: MutableList<String> = mutableListOf()

    lateinit var taxonAdapter: TaxonAdapter
    var taxonId: String = ""
    lateinit var selectedFile: Uri


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
            if (lstProduct.size == 0) {
                Utils.showToast(applicationContext, "Please read csv file before upload!")
            } else {
                // check if all option value are synchronous on server
                //
                currentProduct = 0
                var product = lstProduct.get(0)
                uploadProduct(product)
            }
        }

        btnReadFile.setOnClickListener {
            val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            selectedFile = data?.data!!
            Utils.log(selectedFile.toString())
        }
    }

    fun getAllOptionValuesForCurrentProducts(){
        for (product: Product in lstProduct){
            if (ProductUtils.isStringNotEmpty(product.options!!)){

            }
        }
    }

    fun uploadProduct(product: Product) {
        tvNumber.text = (currentProduct + 1).toString() + "/" + lstProduct.size
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
                        uploadProductImageViaURL(response.body(), 0)
                    } // no need to check here because product always have images
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
        // increase currentProduct for the next upload
        currentProduct++
        if (currentProduct < lstProduct.size) {
            uploadProduct(lstProduct.get(currentProduct))
        } else {
            tvNotice.setText("DONE UPLOADED")
            Utils.showToast(applicationContext, "Uploaded: " + lstProduct.size + " items")
        }
    }

    // find the option value if exist or create a new one
    fun uploadProductVariant(options: Array<String>, index: Int, product: ProductResponse) {
        if (index < options.size) {
            var optionValue = options.get(index)
            optionValue = optionValue.replace("Color", "") // remove color from string
            optionValue = optionValue.replace("Size", "") // remove color from string
            var valueArr = optionValue.split(" ")
            val jsonObject = JsonObject()
            jsonObject.addProperty("name", optionValue)
            jsonObject.addProperty("presentation", optionValue)
            val callResponse = ApiClient().findOrCreateBy("1", Constant.token, jsonObject)
            callResponse.enqueue(object : Callback<OptionValue> {
                override fun onFailure(call: Call<OptionValue>?, t: Throwable?) {
                    var nextVariant = index + 1;
                    if (nextVariant < options.size && ProductUtils.isStringNotEmpty(options.get(nextVariant))) {
                        uploadProductVariant(options, nextVariant, product)
                    }
                }

                override fun onResponse(call: Call<OptionValue>?, response: Response<OptionValue>?) {
                    createVariant(response?.body()?.id, product, options, index)
                }
            })
        }
    }

    // create variant by id return
    fun createVariant(optionValueId: String?, product: ProductResponse, options: Array<String>, index: Int) {
        val variantObject = JsonObject()
        variantObject.addProperty("price", product.price)

        // add option_value_ids array
        val jsonArray = JsonArray()
        jsonArray.add(optionValueId)
        variantObject.add("option_value_ids", jsonArray)

        // add node json variant by using JsonElement
        val productElement = Gson().fromJson(variantObject, JsonElement::class.java)
        val jsonObject = JsonObject()
        jsonObject.add("variant", productElement)

        val callResponse = ApiClient().createVariant(product.slug, Constant.token, jsonObject)
        callResponse.enqueue(object : Callback<Variant> {
            override fun onFailure(call: Call<Variant>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Variant>?, response: Response<Variant>?) {
                var nextVariant = index + 1;
                if (nextVariant < options.size) {
                    uploadProductVariant(options, nextVariant, product)
                } else { // if no variant left, keep upload next product
                    uploadNextProduct()
                }
            }
        })
    }

    fun uploadProductImageViaURL(product: ProductResponse, currentIndex: Int) {
        if (currentIndex < lstImageUrl.size) {
            var url = lstImageUrl.get(currentIndex)
            val jsonObject = JsonObject()
            jsonObject.addProperty("url", url)
            val callResponse = ApiClient().uploadImage(product.slug, Constant.token, jsonObject)
            callResponse.enqueue(object : Callback<ImageResponse> {
                override fun onFailure(call: Call<ImageResponse>?, t: Throwable?) {
                    var index = currentIndex
                    index++
                    if (index < lstImageUrl.size) {
                        // if still have image product, keep upload
                        uploadProductImageViaURL(product, index)
                    } else {// if product have variant, upload varient. if not keep upload product
                        if (ProductUtils.isStringNotEmpty(lstProduct.get(currentProduct).options!!)) {
                            val optionsStr = lstProduct.get(currentProduct).options
                            val options = optionsStr!!.split("/").toTypedArray()
                            uploadProductVariant(options, 0, product)
                        } else {
                            uploadNextProduct()
                        }
                    }

                }

                override fun onResponse(call: Call<ImageResponse>?, response: Response<ImageResponse>?) {
                    if (response!!.isSuccessful) {
                        Utils.glideUrl(applicationContext, response.body().productUrl, imvDownload)
                    }

                    var index = currentIndex
                    index++
                    if (index < lstImageUrl.size) {
                        // if still have image product, keep upload
                        uploadProductImageViaURL(product, index)
                    } else {// if product have variant, upload varient. if not keep upload product
                        if (ProductUtils.isStringNotEmpty(lstProduct.get(currentProduct).options!!)) {
                            val optionsStr = lstProduct.get(currentProduct).options
                            val options = optionsStr!!.split("/").toTypedArray()
                            uploadProductVariant(options, 0, product)
                        } else {
                            uploadNextProduct()
                        }
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

    fun getPath(uri: Uri): String {
        val data = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(applicationContext, uri, data, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    fun readProductDataLib(): List<Product>? {
        // clear before read lines
        lstProduct.clear()

        val inputStream = resources.openRawResource(R.raw.fitness_sportsmartwatch)

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
            if (Utils.indexExists(nextLine, 20)) {
                product.productImage5 = nextLine[20]
            }
            if (Utils.indexExists(nextLine, 21)) {
                product.productImage6 = nextLine[21]
            }
            if (Utils.indexExists(nextLine, 22)) {
                product.productImage7 = nextLine[22]
            }
            if (Utils.indexExists(nextLine, 23)) {
                product.productImage8 = nextLine[23]
            }
            if (Utils.indexExists(nextLine, 24)) {
                product.productImage9 = nextLine[24]
            }
            if (Utils.indexExists(nextLine, 25)) {
                product.productImage10 = nextLine[25]
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
                Utils.log("ERROR")
            }

            override fun onResponse(call: Call<TaxonResponse>?, response: Response<TaxonResponse>?) {
                taxonAdapter.setTaxons(response!!.body().taxons)
            }

        })
    }

    fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().absolutePath + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)

            } else if (isMediaDocument(uri)) { // MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)

            }
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {// MediaStore (and general)
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)

        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {// File
            return uri.path
        }
        return null
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor!!.moveToFirst()) {
                val index = cursor!!.getColumnIndexOrThrow(column)
                return cursor!!.getString(index)
            }
        } finally {
            if (cursor != null)
                cursor!!.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}
