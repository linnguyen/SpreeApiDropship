package com.example.ryne.myapplication.Java.entity.response;

/**
 * Created by ryne on 21/12/2018.
 */

public class ProductResponse {
    private String id;

    private String name;

    private String status;

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
}
