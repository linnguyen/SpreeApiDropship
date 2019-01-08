package com.example.ryne.myapplication.Java.entity.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ryne on 21/12/2018.
 */

public class ImageResponse {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("position")
    @Expose
    private String position;

    @SerializedName("mini_url")
    @Expose
    private String miniUrl;

    @SerializedName("small_url")
    @Expose
    private String smallUrl;

    @SerializedName("product_url")
    @Expose
    private String productUrl;

    @SerializedName("large_url")
    @Expose
    private String largeUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMiniUrl() {
        return miniUrl;
    }

    public void setMiniUrl(String miniUrl) {
        this.miniUrl = miniUrl;
    }

    public String getSmallUrl() {
        return smallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }
}
