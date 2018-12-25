package com.example.ryne.myapplication.Java.entity.response;

/**
 * Created by ryne on 21/12/2018.
 */

public class ProductResponse {
    private String id;

    private String name;

    private String status;

    private String slug;

    public ProductResponse(String id, String name, String status, String slug) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.slug = slug;
    }

    public ProductResponse(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
