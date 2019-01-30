package com.example.ryne.myapplication.Java;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ryne.myapplication.Java.adapter.ReportAdapter;
import com.example.ryne.myapplication.Java.adapter.TaxonAdpater;
import com.example.ryne.myapplication.Java.entity.request.Product;
import com.example.ryne.myapplication.Java.entity.response.ImageResponse;
import com.example.ryne.myapplication.Java.entity.response.ProductResponse;
import com.example.ryne.myapplication.Java.entity.response.Taxon;
import com.example.ryne.myapplication.Java.entity.response.TaxonResponse;
import com.example.ryne.myapplication.Java.localstorage.DAProduct;
import com.example.ryne.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import info.guardianproject.netcipher.NetCipher;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Button btnFetch;
    private Button btnCreate;
    private Button btnReadCSV;
    private Button btnReport;
    private Button btnUploadImage;
    private TextView tvUpload;
    private TextView tvFinish;
    private TextView tvNotice;
    private ImageView imvDownload;
    private TextView tvNumber;
    private Spinner spinner;


    private DownloadTask myTask;
    ApiInterface apiInterface;

    private List<Product> lstProduct;
    private int nextProduct;
    private ArrayList<String> lstUrlPerProduct;

    private DAProduct daProduct;
    private TaxonAdpater taxonAdpater;
    private String taxonId;

    // save fail product into sqlite and then export to recyclerview for result
    // count the number that upload success and fail
    // display the item number that uploaded on UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_jav);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        btnFetch = findViewById(R.id.btnFetch);
        btnCreate = findViewById(R.id.btnCreate);
        btnReadCSV = findViewById(R.id.btnRead);
        btnReport = findViewById(R.id.btnReport);
        tvUpload = findViewById(R.id.tvUpload);
        tvFinish = findViewById(R.id.tvFinish);
        tvNotice = findViewById(R.id.tvNotice);
        imvDownload = findViewById(R.id.imvDownload);
        tvNumber = findViewById(R.id.tvNumber);
        spinner = findViewById(R.id.spinnerTaxon);
        lstProduct = new ArrayList<>();
        daProduct = new DAProduct();


        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<ProductResponse>> call = apiInterface.getProdusts(Constant.token);
                call.enqueue(new Callback<List<ProductResponse>>() {
                    @Override
                    public void onResponse(Call<List<ProductResponse>> call, retrofit2.Response<List<ProductResponse>> response) {
                        Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<List<ProductResponse>> call, Throwable t) {

                    }
                });
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lstProduct.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please read csv file before upload!!", Toast.LENGTH_LONG).show();
                    return;
                }

                // clear data report local
                daProduct.deleteAll(getApplicationContext());
                //
                nextProduct = 0;
                final Product product = lstProduct.get(nextProduct);
                uploadProduct(product);
            }
        });

        btnReadCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                readProductData();
                Toast.makeText(getApplicationContext(), "Click Read", Toast.LENGTH_LONG).show();
                readProductDataLib();
//                readProductData();
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductReportPopup(daProduct.getAll(getApplicationContext()));
            }
        });

        // taxpons spinner
        taxonAdpater = new TaxonAdpater(getApplicationContext());
        spinner.setAdapter(taxonAdpater);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Taxon taxon = (Taxon) adapterView.getItemAtPosition(i);
                taxonId = taxon.getId();
                Toast.makeText(getApplicationContext(), "Selected item: " + taxonId, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getAllTaxons();
    }

    private void uploadProduct(final Product product) {
        tvNumber.setText(nextProduct + "/" + lstProduct.size());

        nextProduct++;
        //
        tvUpload.setText("Uploading: " + product.getId());
        //
        tvNotice.setText("UPLOADING!!");

        JsonObject productJson = new JsonObject();
        productJson.addProperty("id", product.getId());
        productJson.addProperty("name", product.getProductName());
        productJson.addProperty("price", ProductUtils.increasePriceItemRandomly(product.getProductPrice()));
        productJson.addProperty("description", product.getProductDescription1());
        productJson.addProperty("total_on_hand", Constant.TOTAL_IN_HAND);
        productJson.addProperty("shipping_category_id", 1); // Default
        // taxons array
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(taxonId);
        productJson.add("taxon_ids", jsonArray);
        JsonElement productElement = new Gson().fromJson(productJson, JsonElement.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("product", productElement);
        Call<ProductResponse> call = apiInterface.createProduct(Constant.token, jsonObject);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, retrofit2.Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    ProductResponse productResponse = response.body();
//                    uploadProductImageWithStaticURl(productResponse, product);
                    // upload product based on lst URL
                    lstUrlPerProduct = getListImageURL(product);
                    if (!lstUrlPerProduct.isEmpty()) {
//                        downloadImageThenUpload(productResponse.getSlug(), 0);
                        uploadProductImageViaURL(productResponse.getSlug(), 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                // if fail, keep upload
                ProductResponse productFail = new ProductResponse(product.getId(), product.getProductName(), "fail");
                daProduct.add(productFail, getApplicationContext());
                uploadNextProduct();
            }
        });
    }

    private void uploadProductImageViaURL(final String splug, final int currentIndex) {
        if (currentIndex < lstUrlPerProduct.size()) {
            String url = lstUrlPerProduct.get(currentIndex);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("url", url);
            Call<ImageResponse> call = apiInterface.uploadImagev2(splug, Constant.token, jsonObject);
            call.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    if (response.isSuccessful()) {
                        Log.d("SUCCESS", "  COUNT " + nextProduct);
                        // display imv
                        Utils.glideUrl(getApplicationContext(), response.body().getProductUrl(), imvDownload);
                        // increase
                    } else {
                        Log.d("FAIL", "  SOMETHING WENT WRONG WHEN UPLOAD THIS IMAGE " + nextProduct);
                    }
                    int index = currentIndex;
                    index++;
                    if (index < lstUrlPerProduct.size()) {
                        // if still have image product, keep upload
                        uploadProductImageViaURL(splug, index);
                    } else {// keep upload product
                        uploadNextProduct();
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    // if fail keep upload
                    Log.d("FAIL", "number " + nextProduct);
                    int index = currentIndex;
                    index++;
                    if (index < lstUrlPerProduct.size()) {
                        // if still have image product, keep upload
                        uploadProductImageViaURL(splug, index);
                    } else {// keep upload product
                        uploadNextProduct();
                    }
                }
            });
        }
    }

    private void downloadImageThenUpload(final String splug, final int index) {
        if (index < lstUrlPerProduct.size()) {
            String url = lstUrlPerProduct.get(index);
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imvDownload.setImageBitmap(resource);
                            Uri uri = saveImageToInternalStorage(resource);
                            uploadImageV1(uri, splug, index);
                        }
                    });
        }

    }

    private void uploadProductImageWithStaticURl(final ProductResponse productResponse, final Product product) {
        Call<ResponseBody> call = apiInterface.createProductImageUrl(Constant.token, productResponse.getId(), getListImageURL(product));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    tvFinish.setText("Created: [" + nextProduct + "]  " + product.getProductName());
                    //save to db
                    productResponse.setStatus("success");
                    daProduct.add(productResponse, getApplicationContext());
//                    Toast.makeText(getApplicationContext(), "Create product: " + product.getProductName(), Toast.LENGTH_LONG).show();
                    uploadNextProduct();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                productResponse.setStatus("fail image");
                daProduct.add(productResponse, getApplicationContext());
                // if fail, keep upload
                uploadNextProduct();
            }
        });
    }

    private void uploadNextProduct() {
        if (nextProduct < lstProduct.size()) {
            // if list of product still have product, continue upload
            uploadProduct(lstProduct.get(nextProduct));
        } else {
            tvNotice.setText("DONE UPLOADED");
//            List<ProductResponse> lstProduct = daProduct.getAll(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Uploaded: " + lstProduct.size() + " items", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<String> getListImageURL(Product product) {
        ArrayList<String> staticUrls = new ArrayList<>();
        if (Utils.productUrlExist(product.getProductImage1())) {
            staticUrls.add(product.getProductImage1());
        }
        if (Utils.productUrlExist(product.getProductImage2())) {
            staticUrls.add(product.getProductImage2());
        }
        if (Utils.productUrlExist(product.getProductImage3())) {
            staticUrls.add(product.getProductImage3());
        }
        if (Utils.productUrlExist(product.getProductImage4())) {
            staticUrls.add(product.getProductImage4());
        }

        if (Utils.productUrlExist(product.getProductImage5())) {
            staticUrls.add(product.getProductImage5());
        }

        if (Utils.productUrlExist(product.getProductImage6())) {
            staticUrls.add(product.getProductImage6());
        }

        if (Utils.productUrlExist(product.getProductImage7())) {
            staticUrls.add(product.getProductImage7());
        }
        if (Utils.productUrlExist(product.getProductImage8())) {
            staticUrls.add(product.getProductImage8());
        }

        if (Utils.productUrlExist(product.getProductImage9())) {
            staticUrls.add(product.getProductImage9());
        }

        if (Utils.productUrlExist(product.getProductImage10())) {
            staticUrls.add(product.getProductImage10());
        }
        return staticUrls;
    }

    private void readProductDataLib() {
        // clear all product from list
        lstProduct.clear();
        // Read the raw csv file
        InputStream is = getResources().openRawResource(R.raw.product_test);

        // Reads text from character-input stream, buffering characters for efficient reading
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is)
        );

        CSVReader csvReader = new CSVReader(reader);


        String[] nextLine;
        boolean firstLine = true;
        int count = 0;
        try {
            while ((nextLine = csvReader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                Log.d("Line: ", " " + csvReader.getLinesRead());

                count++;

                Product product = new Product();
                // Setters
                product.setId(nextLine[0]);
                product.setProductName(nextLine[1]);
                product.setCategoryName(nextLine[2]);
                product.setCategoryUrl(nextLine[3]);
                product.setSubCategoryName(nextLine[4]);
                product.setSubCategoryUrl(nextLine[5]);
                product.setDateProductWasLaunched(nextLine[6]);
                product.setCurrency(nextLine[7]);
                product.setProductPrice(nextLine[8]);
                product.setWeight(nextLine[9]);
                product.setProductDescription1(nextLine[10]);
                product.setProductDescription2(nextLine[11]);
                product.setProductDescription3(nextLine[12]);
                product.setProductUrl(nextLine[13]);
                product.setWarehouse(nextLine[14]);
                product.setOptions(nextLine[15]);
                if (Utils.indexExists(nextLine, 16)) {
                    product.setProductImage1(nextLine[16]);
                }
                if (Utils.indexExists(nextLine, 17)) {
                    product.setProductImage2(nextLine[17]);
                }
                if (Utils.indexExists(nextLine, 18)) {
                    product.setProductImage3(nextLine[18]);
                }
                if (Utils.indexExists(nextLine, 19)) {
                    product.setProductImage4(nextLine[19]);
                }
                lstProduct.add(product);

                // Log the object
                Log.d("PRODUCT" + count, " " + product.toString());
            }
            Toast.makeText(getApplicationContext(), "Total: " + lstProduct.size(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readProductData() {
        // clear list product
        lstProduct.clear();
        // Read the raw csv file
        InputStream is = getResources().openRawResource(R.raw.edc_emergenkit);

        // Reads text from character-input stream, buffering characters for efficient reading
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );


        // Initialization
        String line = "";

        // Initialization
        try {
            // Step over headers
            reader.readLine();

            // If buffer is not empty
            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity", "Line: " + line);
                // use comma as separator columns of CSV
//                String[] tokens = line.split(",");
                String[] tokens = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                // Read the data
                Product product = new Product();
                // Setters
                product.setId(tokens[0]);
                product.setProductName(tokens[1]);
                product.setCategoryName(tokens[2]);
                product.setCategoryUrl(tokens[3]);
                product.setSubCategoryName(tokens[4]);
                product.setSubCategoryUrl(tokens[5]);
                product.setDateProductWasLaunched(tokens[6]);
                product.setCurrency(tokens[7]);
                product.setProductPrice(tokens[8]);
                product.setWeight(tokens[9]);
                product.setProductDescription1(tokens[10]);
                product.setProductDescription2(tokens[11]);
                product.setProductDescription3(tokens[12]);
                product.setProductUrl(tokens[13]);
                product.setWarehouse(tokens[14]);
                product.setOptions(tokens[15]);

                if (Utils.indexExists(tokens, 16)) {
                    product.setProductImage1(tokens[16]);
                }
                if (Utils.indexExists(tokens, 17)) {
                    product.setProductImage2(tokens[17]);
                }
                if (Utils.indexExists(tokens, 18)) {
                    product.setProductImage3(tokens[18]);
                }
                if (Utils.indexExists(tokens, 19)) {
                    product.setProductImage4(tokens[19]);
                }
//                product.setProductImage1(tokens[16].substring(1, tokens[16].length() - 1));
//                product.setProductImage2(tokens[17].substring(1, tokens[17].length() - 1));
//                product.setProductImage3(tokens[18].substring(1, tokens[18].length() - 1));
//                product.setProductImage4(tokens[19].substring(1, tokens[19].length() - 1));
//                product.setProductImage5(tokens[20]);
                // Adding object to a class
                lstProduct.add(product);

                // Log the object
                Log.d("My Activity", "Just created: " + product);
            }

            Toast.makeText(getApplicationContext(), "Total: " + lstProduct.size(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            // Logs error with priority level
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);

            // Prints throwable details
            e.printStackTrace();
        }
    }

    public void showProductReportPopup(List<ProductResponse> lstProduct) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();

        //this is custom dialog
        //custom_popup_dialog contains textview only
        View customView = layoutInflater.inflate(R.layout.custom_popup_dialog, null);
        // reference recylerview
        RecyclerView rcvReport = customView.findViewById(R.id.rcv_report);
        rcvReport.setHasFixedSize(true);
        rcvReport.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ReportAdapter adapter = new ReportAdapter(lstProduct, getApplicationContext());
        adapter.setData(lstProduct);
        rcvReport.setAdapter(adapter);

        builder.setView(customView);
        builder.create();
        builder.show();
    }

    private void loadImageFromUrlWithGlide(String url, final String productId, final int currentIndex) {
//        Glide.with(getApplicationContext())
//                .asBitmap()
//                .load(url)
//                .into(new SimpleTarget<Bitmap>() {
//
//                    @Override
//                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        imvDownload.setImageBitmap(resource);
//
//                        // Save bitmap to internal storage
//                        Uri imageInternalUri = saveImageToInternalStorage(resource);
//
//                        // upload the image to server Spree
//                        uploadImageV1(imageInternalUri, productId, currentIndex);
//                    }
//                });
    }


    private class DownloadTask extends AsyncTask<URL, String, Bitmap> {

        private String productId;
        private int currentIndex;

        public DownloadTask(String productId, int index) {
            this.productId = productId;
            this.currentIndex = index;
        }

        protected void onPreExecute() {
            //mProgressDialo g.show();
        }

        // Do the task in background/non UI thread
        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[0];
            HttpURLConnection connection = null;

            try {
                // Initialize a new http url connection
//                connection = (HttpURLConnection) url.openConnection();
                connection = NetCipher.getHttpsURLConnection(url);

                // Connect the http url connection
                connection.connect();

                // Get the input stream from http url connection
                InputStream inputStream = connection.getInputStream();

                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                // Convert BufferedInputStream to Bitmap object
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                // Return the downloaded bitmap
                return bmp;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Disconnect the http url connection
                connection.disconnect();
            }
            return null;
        }

        // When all async task done
        protected void onPostExecute(Bitmap result) {
            // Hide the progress dialog

            if (result != null) {
                // Display the downloaded image into ImageView
                imvDownload.setImageBitmap(result);

                // Save bitmap to internal storage
                Uri imageInternalUri = saveImageToInternalStorage(result);

                // upload the image to server Spree
                uploadImageV1(imageInternalUri, productId, currentIndex);

                // increase the
                currentIndex++;
                if (currentIndex < lstUrlPerProduct.size()) {
                    // if still have image product, keep upload
                    downloadImageThenUpload(productId, currentIndex++);
                } else {// keep upload product
                    uploadNextProduct();
                }

            } else {
                // Notify user that an error occurred while downloading image\
                Toast.makeText(getApplicationContext(), "Error occured while downloading image: " + productId, Toast.LENGTH_LONG).show();
            }
        }

    }

    // Custom method to convert string to url
    protected URL stringToURL(String urlString) {
        try {
            URL url = new URL(urlString);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Custom method to save a bitmap into internal storage
    protected Uri saveImageToInternalStorage(Bitmap bitmap) {
        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());

        // Initializing a new file
        // The bellow line return a directory in internal storage
        File file = wrapper.getDir("Images", MODE_PRIVATE);

        // Create a file to save the image
        file = new File(file, "UniqueFileName" + ".jpg");

        try {
            // Initialize a new OutputStream
            OutputStream stream = null;

            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(file);

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();

        } catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());

        // Return the saved image Uri
        return savedImageURI;
    }

    private void uploadImage(Uri uriImagepath) {
        File file = new File(uriImagepath.getPath());
        final RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        Call<ResponseBody> call = apiInterface.uploadImage(Constant.token, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Upload image success", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void uploadImageV1(final Uri imageUri, final String productId, final int currentIndex) {
        File file = new File(imageUri.getPath());
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image"), file);
        builder.addFormDataPart("image[attachment]", file.getName(),
                fileBody);
        Call<ResponseBody> call = apiInterface.uploadImagev1(productId, Constant.token, builder.build());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Upload image success", Toast.LENGTH_LONG).show();
                    // increase
                    int index = currentIndex;
                    index++;
                    if (index < lstUrlPerProduct.size()) {
                        // if still have image product, keep upload
                        downloadImageThenUpload(productId, index);
                    } else {// keep upload product
                        uploadNextProduct();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getAllTaxons() {
        Call<TaxonResponse> call = apiInterface.getTaxons(Constant.token, true);
        call.enqueue(new Callback<TaxonResponse>() {
            @Override
            public void onResponse(Call<TaxonResponse> call, Response<TaxonResponse> response) {
                if (response.isSuccessful()) {
                    taxonAdpater.setTaxons(response.body().getTaxons());
                }
            }

            @Override
            public void onFailure(Call<TaxonResponse> call, Throwable t) {

            }
        });
    }
}

