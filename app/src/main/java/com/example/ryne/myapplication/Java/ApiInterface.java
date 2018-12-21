package com.example.ryne.myapplication.Java;

import com.example.ryne.myapplication.Java.entity.response.ProductResponse;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by ryne on 21/12/2018.
 */

public interface ApiInterface {
    // The method for testing server
    @GET("v1/products")
    Call<List<ProductResponse>> getProdusts(@Query("token") String token);

    @POST("v1/products")
    Call<ProductResponse> createProduct(@Query("token") String token, @Body JsonObject jsonObject);

    @FormUrlEncoded
    @POST("/api/v1/image_url")
    Call<ResponseBody> createProductImageUrl(@Query("token") String token, @Query("id") String id, @Field("static_urls[]") ArrayList<String> static_urls);

//    @Multipart
//    @POST("v1/products/glass-of-ryne-ne/images")
//    Call<ResponseBody> upload(
//            @Part("description") RequestBody description,
//            @Part MultipartBody.Part file
//    );

//    @POST("v1/products/glass-of-ryne-ne/images/")
//    Call<ResponseBody> uploadImage(@Query("token") String token,@Body RequestBody image);
}
